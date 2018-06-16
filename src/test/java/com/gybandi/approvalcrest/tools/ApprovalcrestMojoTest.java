package com.gybandi.approvalcrest.tools;

import static com.github.karsaig.approvalcrest.matcher.Matchers.sameBeanAs;
import static com.github.karsaig.approvalcrest.matcher.Matchers.sameJsonAsApproved;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Ignore;
import org.junit.Test;

import com.gybandi.approvalcrest.tools.pojo.TestPojo;

public class ApprovalcrestMojoTest {

	/* START - Dummy tests for JSON discovery */
	@Test
	@Ignore
	public void testWithValidJson1() {
		List<TestPojo> pojos = dummyPojos();
		assertThat(pojos, sameJsonAsApproved());
	}

	@Test
	@Ignore
	public void testWithValidJson2() {
		List<TestPojo> pojos = dummyPojos();
		assertThat(pojos, sameJsonAsApproved().withUniqueId("uniqueId"));
	}

	@Test
	@Ignore
	public void testWithValidJsonWithoutFileComment3() {
		List<TestPojo> pojos = dummyPojos();
		assertThat(pojos, sameJsonAsApproved().withFileName("customFile2"));
	}

	@Test
	@Ignore
	public void testWithValidJson3() {
		List<TestPojo> pojos = dummyPojos();
		assertThat(pojos, sameJsonAsApproved().withFileName("customFile"));
	}

	private List<TestPojo> dummyPojos() {
		TestPojo testPojo = new TestPojo(1L, "dummyPojo", 2, true, null);
		TestPojo testPojo2 = new TestPojo(2L, "dummyPojo2", 3, false, testPojo);
		TestPojo testPojo3 = new TestPojo(3L, "dummyPojo3", 4, false, testPojo2);

		List<TestPojo> pojos = Arrays.asList(testPojo, testPojo2, testPojo3);
		return pojos;
	}
	/* END - Dummy tests for JSON discovery */

	@Test
	public void testDiscoverShouldListAllJsonFiles() throws MojoExecutionException, MojoFailureException {
		// GIVEN
		List<String> expectedFiles = Arrays.asList("97bf67-uniqueId-not-approved.json", "7be8b0-not-approved.json",
				"999999-approved.json");
		// WHEN
		List<String> result = getDiscoveredFiles();
		// THEN
		assertThat(result, sameBeanAs(expectedFiles));
	}

	private List<String> getDiscoveredFiles() throws MojoExecutionException, MojoFailureException {
		List<String> filesToDelete = new ArrayList<>();
		AbstractApprovalcrestMojo underTest = new AbstractApprovalcrestMojo() {

			@Override
			public void handleJsonFile(File file) {
				filesToDelete.add(file.getName());
			}
		};
		underTest.execute();
		return filesToDelete;
	}
}
