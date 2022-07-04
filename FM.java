package codegen.util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class FileMergeUtil {
	static int count = 0;
	public static  void merge(File dir, File targetFile) throws IOException {
		RegexFileFilter regexFileFilter = new RegexFileFilter(".+\\.(java|html|yml|xml)");
		Collection<File> collection = FileUtils
				.listFiles(dir, regexFileFilter, DirectoryFileFilter.DIRECTORY);
		for(File file : collection){
			ArrayList lines = new ArrayList();
			lines.add(String.format("----strat of file %s---", file.getAbsolutePath()));
			try {
				List<String> list = FileUtils.readLines(file, "UTF-8");
				for(String s : list){
					if(!StringUtils.isBlank(s) && !s.trim().startsWith("//")){
						lines.add(s);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileUtils.writeLines(targetFile, lines, true);
		}
	}

	public static  void extract(File srcFile, File targetDir, String srcFileRootPath) throws IOException{
		List<String> list = FileUtils.readLines(srcFile, StandardCharsets.UTF_8);
		ArrayList fileLines = new ArrayList();
		Pattern pattern = Pattern
				.compile("----strat of file(.+)---$", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		File targetFile = null;
		srcFileRootPath = srcFileRootPath.replaceAll("\\\\", "\\\\\\\\");
		for(String line : list){
			if(!StringUtils.isBlank(line) && line.trim().startsWith("----strat of file")){

				write2File(fileLines, targetFile);
				String fileName;
				Matcher matcher = pattern.matcher(line);
				boolean b = matcher.find();
				if(b){
					fileName = matcher.group(1);
				}else{
					throw new IOException("cannot extract fileName for "+ line);
				}
				String newFileNamepart = fileName.trim().replaceFirst(srcFileRootPath, "");;
				targetFile = new File(targetDir + File.separator + newFileNamepart);
				fileLines = new ArrayList();
			}else if(!StringUtils.isBlank(line)){
				fileLines.add(line);
			}
		}
		write2File(fileLines, targetFile);
		return;
	}
	public static void write2File(ArrayList fileLines, File targetFile)
			throws IOException {
		if(!fileLines.isEmpty()){
			FileUtils.writeLines(targetFile, fileLines, false);
			count++;
			System.out.println("writting the " + count+ " file "+targetFile.getName() + " done");
		}
	}
	public static void main(String[] args) throws IOException {
		File dir = new File("D:\\projects\\cg\\cg-min\\src");
		File targetFile = new File("D:\\projects\\cg\\cg-min\\all.java");
		merge(dir,targetFile);
		extract(targetFile, new File("D:\\projects\\cg\\test"), "D:\\projects\\cg\\cg-min");
	}

}
