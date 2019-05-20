package com.H.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 使用Java调用FFMpeg
 * @author H
 *
 */
public class FFMpeg {

	/**
	 * ffmpeg.exe 所在的路径
	 */
	private String ffmpegEXE;

	public FFMpeg(String ffmpegEXE) {
		this.ffmpegEXE = ffmpegEXE;
	}

	/**
	 * 
	 * @description 视频转码
	 * 
	 */
	public void convertor(String videoInputPath, String videoOutputPath) throws IOException {
		// ffmpeg -i input.mp4 output.avi
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);

		command.add("-i");
		command.add(videoInputPath);
		command.add(videoOutputPath);
		for (String string : command) {
			System.out.print(string + " ");
		}
		System.out.println();
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();

		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		while ((bufferedReader.readLine()) != null) ;

		if (bufferedReader != null)
			bufferedReader.close();
		if (inputStreamReader != null)
			inputStreamReader.close();
		if (errorStream != null)
			errorStream.close();
	}

	/**
	 * @description 视频消音
	 * @return 消音后视频路径
	 * @throws IOException
	 */
	public String videoMuffling(String videoPath) throws IOException {
		// ffmpeg -i /path/to/input.mp4 -c:v copy -an /path/to/input-no-audio.mp4
		//处理输出路径 源文件后+"_mute" 如 C:\\test.mp4 ==> C:\\test_mute.mp4
		String[] path = videoPath.split("\\.");
		String outPath = path[0];
		for(int i = 1 ; i < path.length - 1 ; i++) {
			outPath += "." + path[i];
		}
		outPath += "_mute." + path[path.length-1];
		
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);
		command.add("-i");
		command.add(videoPath);
		command.add("-c:v");
		command.add("copy");
		command.add("-an");
		command.add(outPath);
		for (String string : command) {
			System.out.print(string + " ");
		}
		System.out.println();
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();

		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		while ((bufferedReader.readLine()) != null) ;

		if (bufferedReader != null)
			bufferedReader.close();
		if (inputStreamReader != null)
			inputStreamReader.close();
		if (errorStream != null)
			errorStream.close();

		
		//删除原文件
		File file = new File(videoPath);
		if(file.exists() && file.isFile()) {
			file.delete();
		}
		return outPath;
	}

	/**
	 * @description 将视频和音频合并
	 */
	public void mergeVideoMusic(String videoInputPath, String musicInputPath, double seconds, String videoOutputPath)
			throws IOException {
		// ffmpeg -i input.mp4 -i music.mp3 -t 8 -y output.mp4
		
		//先将原视频消音
		String path = videoMuffling(videoInputPath);
		
		List<String> command = new ArrayList<>();
		command.add(ffmpegEXE);

		command.add("-i");
		command.add(path);

		command.add("-i");
		command.add(musicInputPath);

		command.add("-t");
		command.add(String.valueOf(seconds));

		command.add("-y");
		command.add(videoOutputPath);
		for (String string : command) {
			System.out.print(string + " ");
		}
		System.out.println();
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();

		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		while ((bufferedReader.readLine()) != null) ;

		if (bufferedReader != null)
			bufferedReader.close();
		if (inputStreamReader != null)
			inputStreamReader.close();
		if (errorStream != null)
			errorStream.close();
		
		//删除原文件
		File file = new File(path);
		if(file.exists() && file.isFile()) {
			file.delete();
		}
	}
	/**
	 * @description 生成封面
	 * @param videoInputPath
	 * @param coverOutputPath
	 * @throws IOException 
	 */
	public void getCover (String videoInputPath , String coverOutputPath) throws IOException {
		//ffmpeg.exe -ss 00:00:01 -y -i test.mp4 -vframes 1 new.jpg
		List<String> command = new ArrayList<String>();
		command.add(ffmpegEXE);
		command.add("-ss");
		command.add("00:00:01");
		command.add("-y");
		command.add("-i");
		command.add(videoInputPath);
		command.add("-vframes");
		command.add("1");
		command.add(coverOutputPath);
		for (String string : command) {
			System.out.print(string + " ");
		}
		System.out.println();
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		
		while ((bufferedReader.readLine()) != null) ;

		if (bufferedReader != null)
			bufferedReader.close();
		if (inputStreamReader != null)
			inputStreamReader.close();
		if (errorStream != null)
			errorStream.close();
	}
	
//	public static void main(String[] args) {
//		FFMpeg ffMpeg = new FFMpeg("D:\\Java\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			ffMpeg.getCover("C:\\Users\\H\\Desktop\\新建文件夹\\test.mp4", "C:\\Users\\H\\Desktop\\新建文件夹\\new.jpg");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) {
//		FFMpeg ffMpeg = new FFMpeg("D:\\Java\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			ffMpeg.convertor( "C:\\Users\\H\\Desktop\\新建文件夹\\test2.avi", "C:\\Users\\H\\Desktop\\新建文件夹\\test3.mp4");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) {
//		FFMpeg ffMpeg = new FFMpeg("D:\\Java\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			ffMpeg.mergeVideoMusic("C:\\Users\\H\\Desktop\\新建文件夹\\test.mp4", "C:\\Users\\H\\Desktop\\新建文件夹\\music.mp3",
//					8.1, "C:\\Users\\H\\Desktop\\新建文件夹\\out.mp4");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void main(String[] args) {
//		FFMpeg ffMpeg = new FFMpeg("D:\\Java\\ffmpeg\\bin\\ffmpeg.exe");
//		try {
//			String path = ffMpeg.videoMuffling("C:\\Users\\H\\Desktop\\新建文件夹\\test.mp4");
//			System.out.println("\n" + path);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
