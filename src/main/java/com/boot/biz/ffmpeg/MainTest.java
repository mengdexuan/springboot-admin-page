package com.boot.biz.ffmpeg;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author mengdexuan on 2021/8/13 17:42.
 */
@Slf4j
public class MainTest {


	public static void main(String[] args) {
//		String filePath = "/Users/hetao/Desktop/Ensoniq-ZR-76-01-Dope-77.wav";
		String filePath = "C:\\Users\\18514\\Desktop\\test5\\a.mp3";

		String result = getAudioInformation(filePath);

		String time = "1:22";


//		duration	QRTZ_TRIGGERS

		System.out.println(result);
	}



	public static String getAudioInformation(String filePath) {
		Runtime run = null;
		Process p = null;
		try {
			run = Runtime.getRuntime();
			p = run.exec("ffprobe.exe -v quiet -show_format -show_streams -print_format json " + filePath);
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				sb.append(lineStr);
			}
			if (p.waitFor() != 0) {
				if (p.exitValue() == 1)
					log.error("命令执行失败!");
				return null;
			}
			return sb.toString();
		} catch (Exception e) {
			log.error("msg=getAudioInformation异常：{}", e.getMessage(), e);
		} finally {
			try {
				p.getOutputStream().close();
				p.getInputStream().close();
				p.getErrorStream().close();
				run.freeMemory();
			} catch (IOException e) {
				log.error("msg=getAudioInformation异常：{}", e.getMessage(), e);
			}
		}
		return null;
	}


	/**
	 * "format": {
	 *     "filename": "C:\\Users\\18514\\Desktop\\test5\\a.mp3",
	 *     "nb_streams": 1,
	 *     "nb_programs": 0,
	 *     "format_name": "mp3",
	 *     "format_long_name": "MP2/3 (MPEG audio layer 2/3)",
	 *     "start_time": "0.025057",
	 *     "duration": "82.938776",
	 *     "size": "3318778",
	 *     "bit_rate": "320118",
	 *     "probe_score": 51,
	 *     "tags": {
	 *       "artist": "川井宪次",
	 *       "encoder": "Lavf56.4.101",
	 *       "genre": "Soundtrack",
	 *       "title": "大団円",
	 *       "album": "02.武侠音乐系列之缠绵悱恻",
	 *       "track": "15",
	 *       "date": "1992"
	 *     }
	 *   }
	 */
}
