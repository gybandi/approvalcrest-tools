package com.gybandi.approvalcrest.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class AbstractApprovalcrestMojo extends AbstractMojo {

	private static final String ID_SEPARATOR = "@";
	private static final String TEST_DIRECTORY = "./src/test";
	private static final String JSON_COMMENT_OPEN_TAG = "/*";
	private static final String JSON_COMMENT_CLOSE_TAG = "*/";
	private static final String NOT_APPROVED_JSON_SUFFIX = "not-approved.json";
	private static final String JSON_SUFFIX = ".json";
	private Map<String, File> markedForDelete = new HashMap<>();

	public void execute() throws MojoExecutionException, MojoFailureException {
		File currentDirFile = new File(TEST_DIRECTORY);
		try {
			List<Path> allFilePaths = Files.walk(Paths.get(currentDirFile.getAbsolutePath()))
					.filter(Files::isRegularFile).filter(f -> f.toFile().getName().endsWith(JSON_SUFFIX))
					.collect(Collectors.toList());
			logList(allFilePaths);

			markForDelete(findOrphanedJsonFilesByComment(allFilePaths));
			markForDelete(findNotApprovedFiles(allFilePaths));
			getLog().info("Discovering finished. Executing handler logic on discovered files...");
			for (String key : markedForDelete.keySet()) {
				handleJsonFile(markedForDelete.get(key));
			}

		} catch (IOException e) {
			getLog().error("Error during recursive file discovery!", e);
			throw new MojoFailureException(e.getMessage());
		}
	}

	/**
	 * Defines what to do with the discovered json file.
	 * 
	 * @param file
	 *            the discovered json file
	 */
	protected abstract void handleJsonFile(File file);

	/**
	 * Finds all not-approved json files.
	 * 
	 * @param allFilePaths
	 *            all the json files' path
	 * @return a filtered list of file paths
	 */
	private List<Path> findNotApprovedFiles(List<Path> allFilePaths) {
		return allFilePaths.stream().filter(p -> p.toFile().getName().endsWith(NOT_APPROVED_JSON_SUFFIX))
				.collect(Collectors.toList());
	}

	/*
	 * TODO: filter out files by examining the existing hashes. Generate all
	 * hashes by checking @Test annotated methods, then filter out the ones that
	 * are absent from this list.
	 */
	private void filterOutByExistingTestHashes() {
		File currentDirFile = new File(TEST_DIRECTORY);
	}

	/**
	 * Finds all orphaned json files, where the first line comment points to a
	 * non existing test class/method. json files without first line comment
	 * won't be touched.
	 * 
	 * @param allFilePaths
	 *            all the json files' path
	 * @return a filtered list of file paths
	 */
	private List<Path> findOrphanedJsonFilesByComment(List<Path> allFilePaths) {
		List<Path> result = new ArrayList<>();
		for (Path p : allFilePaths) {
			try (Stream<String> stream = Files.lines(p)) {
				String firstLine = stream.findFirst().get();
				if (firstLine != null && firstLine.startsWith(JSON_COMMENT_OPEN_TAG)
						&& firstLine.trim().endsWith(JSON_COMMENT_CLOSE_TAG)) {
					String classAndMethodName = firstLine.replace(JSON_COMMENT_OPEN_TAG, "")
							.replace(JSON_COMMENT_CLOSE_TAG, "");
					String className = classAndMethodName.substring(0, classAndMethodName.lastIndexOf("."));
					String methodName = classAndMethodName.substring(classAndMethodName.lastIndexOf(".") + 1,
							classAndMethodName.length());
					getLog().debug(className);
					getLog().debug(methodName);
					try {
						Class<?> clazz = Class.forName(className);
						clazz.getDeclaredMethod(methodName);
					} catch (ClassNotFoundException | NoSuchMethodException e) {
						getLog().debug(String.format("No class file/method found: %s Marking for delete: %s",
								classAndMethodName, p.toFile().getName()));
						result.add(p);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private void markForDelete(List<Path> paths) {
		paths.stream().forEach(p -> markForDelete(p));
	}

	private void markForDelete(Path p) {
		markedForDelete.put(createIdFromPath(p), p.toFile());
	}

	private String createIdFromPath(Path path) {
		String fileName = path.toFile().getName();
		return path.getParent().toFile().getName() + ID_SEPARATOR + fileName.substring(0, fileName.indexOf("-"));
	}

	private void logList(List<Path> listOfPaths) {
		listOfPaths.stream().forEach(p -> getLog().info(p.toFile().getName()));
	}

}
