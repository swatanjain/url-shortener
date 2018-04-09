package org.urlshortener;

/**
 * General purpose application error.
 * @since 1.0
 **/
public class ApplicationError extends Error {

	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	public ApplicationError(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationError(String message) {
		super(message);
	}

}
