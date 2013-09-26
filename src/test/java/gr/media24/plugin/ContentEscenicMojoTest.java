package gr.media24.plugin;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;



public class ContentEscenicMojoTest {
	
	private ContentEscenicMojo contentEscenicMojo = null;
	
	@Before
	public void initialize(){
		contentEscenicMojo = new ContentEscenicMojo();
	}
	
	@Test(expected=MojoFailureException.class)
	public void noPublicationResources() throws MojoExecutionException, MojoFailureException{
		contentEscenicMojo.setPublicationResources(new File("./temp"));
		
		File file = new File(ContentEscenicMojoTest.class.getResource("./WidgetsFolder").getPath()); 
		contentEscenicMojo.setWidgetsDirectory(file);
		contentEscenicMojo.execute();
	}
	
	@Test(expected=MojoFailureException.class)
	public void noWidgetsDirectory() throws MojoExecutionException, MojoFailureException{
		contentEscenicMojo.setWidgetsDirectory(new File("./temp"));

		File file = new File(ContentEscenicMojoTest.class.getResource("./PublicationResources.file").getPath()); 
		contentEscenicMojo.setPublicationResources(file);
		contentEscenicMojo.execute();
	}
	
	@Test
	public void testExecute() throws MojoExecutionException, MojoFailureException{
		File file = new File(ContentEscenicMojoTest.class.getResource("./WidgetsFolder").getPath()); 
		contentEscenicMojo.setWidgetsDirectory(file);
		file = new File(ContentEscenicMojoTest.class.getResource("./PublicationResources.file").getPath()); 
		contentEscenicMojo.setPublicationResources(file);
		contentEscenicMojo.execute();
	}
}
