package org.urlshortener.dataaccess;

import static org.urlshortener.ApplicationConstants.DOT;
import static org.urlshortener.ApplicationConstants.JSON_FILE_SUFFIX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.io.FileUtils;
import org.springframework.hateoas.Identifiable;
import org.urlshortener.ApplicationError;
import org.urlshortener.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Back-end repository for caching and persisting elements created by various controller components.
 * 	Implements basic CRUD operations of persistence storage. This class also includes a LRU caching 
 * 	mechanism for minimizing disk operations while performing READ operation. <p> {@link RepositoryAccessAspect}
 *  is the helper class which helps to auto-commit the create/update operations.</p>
 * 
 * @param <T> the type of elements that need to be persisted. Must implement {@link Identifiable}.
 * @since 1.0
 **/
@org.springframework.stereotype.Repository
public abstract class BaseRepository<T extends Identifiable<String>> {

	/*
	 * Fact that we're creating completely dis-integrated and distributed records into the file system
	 * 	for each element entity with embedded caching mechanism, hence not implementing	JDBC datasource
	 *	repository shouldn't demerit the scalability/performance requirements. Also, if required Hadoop's
	 *	HDFS system can be explored as the persistent storage with minimal changes to achieve desired results.
	 */
	private Map<String, T> cache = new LRUMap<>();
	private final File repo;

	private ObjectMapper objectMapper = new ObjectMapper();

	public BaseRepository(String repoPath) {

		this.repo = new File(repoPath);
		if( !repo.exists() ) {
			//Create initial repo directory if not exists
			repo.mkdirs();
		} 

	}

	/**
	 * Deserializing of the element requires type information
	 * 	to be available at runtime. Hence, all implementations
	 * 	must provide the type information at runtime by implementing
	 * 	this method.
	 **/
	protected abstract Class<T> getType();

	public synchronized void create(T element) {
		cache.put(element.getId(), element);
	}

	public synchronized Optional<T> read(String id) {

		//Check in cache first
		T element = cache.get(id);
		if (element == null) {

			//If not found in cache, load from persistent storage.
			File file = getElementFile(id);
			if (file.exists()) {
				element = deserialize(file);
				cache.put(id, element);
			}

		}
		return Optional.ofNullable(element);

	}	

	public synchronized boolean update(T updatedElement) {

		if (updatedElement != null) {
			String id = updatedElement.getId();
			Optional<T> element = read(id);
			if (element.isPresent()){
				cache.put(id, updatedElement);
				return true;
			} 
		}
		return false;

	}

	public synchronized boolean delete(T element) throws IOException {

		/* 
		 * Commit operation implementation here simply dumps the serialized body of the
		 * 	element to the file system.
		 * Now since create/update operations are auto-committed, this ensures that the
		 * 	element is persisted into the storage as soon as it is added to the cache.
		 * 	Commit implementation i.e. dumping the serialized body won't help in case of 
		 * 	delete, in fact we need exactly opposite of that. Hence, delete operation must
		 * 	delete the respective file manually to ensure consistency.
		 */
		String id = element.getId();
		File file = getElementFile(element.getId());
		if ( cache.containsKey(id) || file.exists() ) {
		
			cache.remove(id);
			file.delete();
			return true;
			
		}
		return false;

	}	

	/**
	 * Returns an {@link Iterator} instance to iterate over all the elements
	 *  stored in the persistent storage.
	 **/
	public ElementIterator<T> getIterator() {

		return new ElementIterator<>( 
				repo,
				new String[]{JSON_FILE_SUFFIX}, 
				false);
	}

	/**
	 * Persists the element data to the back-end repository.
	 * @param element the element to be persisted. Must implement {@link Identifiable}
	 **/
	public void commit(T element) throws IOException {

		File file = getElementFile(element.getId());
		try ( OutputStream out = new FileOutputStream(file)) {
			out.write(element.toString().getBytes());
		}

	}

	private File getElementFile(String id) {

		return new File( repo.getAbsolutePath()
				+ File.separator
				+ id
				+ DOT 
				+ JSON_FILE_SUFFIX );

	}

	private T deserialize(File file) {

		T element = null;
		try (FileInputStream fis = new FileInputStream(file)) {

			element = objectMapper.readValue(fis, getType());

		} catch (IOException e) {

			Logger.error("Unable to parse the repository file: " + file);
			throw new ApplicationError("Unable to parse the repository file: " + file, e);

		}
		return element;

	}

	private class ElementIterator<E extends T> implements java.util.Iterator<T> {

		private java.util.Iterator<File> fileIterator;

		@SuppressWarnings("unchecked")
		private ElementIterator(File directory, String[] extensions, boolean recursive) {
			fileIterator = FileUtils.iterateFiles(directory, extensions, recursive);
		}

		@Override
		public boolean hasNext() {
			return fileIterator.hasNext();
		}

		@Override
		public T next() {

			File file = fileIterator.next();
			return deserialize(file);
		}

	}

}
