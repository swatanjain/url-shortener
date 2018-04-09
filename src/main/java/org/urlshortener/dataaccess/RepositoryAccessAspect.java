package org.urlshortener.dataaccess;

import static org.urlshortener.ApplicationConstants.APPLICATION_BASE_PACKAGE;

import java.io.IOException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.urlshortener.model.BaseModel;

/**
 * Helps auto-commit the write operations on repository access.
 * 
 * @since 1.0
 * @see BaseRepository
 **/
@Aspect
@Configuration
public class RepositoryAccessAspect<T extends BaseModel> {

	@SuppressWarnings("unchecked")
	@After("execution(* " + APPLICATION_BASE_PACKAGE + ".dataaccess.BaseRepository.create(..)) "
			+ "|| execution(* " + APPLICATION_BASE_PACKAGE + ".dataaccess.BaseRepository.update(..))")
	public void afterWriteOperation(JoinPoint joinPoint) throws IOException {

		T element = (T)joinPoint.getArgs()[0];
		((BaseRepository<T>)joinPoint.getThis()).commit(element);

	}

}
