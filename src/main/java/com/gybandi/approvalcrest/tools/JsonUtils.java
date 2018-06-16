package com.gybandi.approvalcrest.tools;

public final class JsonUtils {

	private JsonUtils() {
	}

	public boolean isTestMethod(final StackTraceElement element) {
		boolean isTest = false;

		String fullClassName = element.getClassName();
		Class<?> clazz;
		try {
			clazz = Class.forName(fullClassName);
			// isTest =
			// clazz.getMethod(element.getMethodName()).isAnnotationPresent(Test.class);

		} catch (Throwable e) {
			isTest = false;
		}

		return isTest;
	}

}
