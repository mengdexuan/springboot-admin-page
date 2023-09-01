package com.boot.biz.file.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.file.dto.FileContent;
import com.boot.biz.file.dto.FileTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengdexuan on 2017/6/22 11:13.
 */
@Controller("fileController2")
@RequestMapping("/file")
@Slf4j
public class FileController {

	private static OsInfo osInfo = SystemUtil.getOsInfo();

	@Value("${can-edit-ext}")
	private String canEditExtStr;
	private List<String> canEditExtList;

	@PostConstruct
	private void init() {
		canEditExtList = HelpMe.easySplit(canEditExtStr);
	}


	@RequestMapping("/parentDir")
	@ResponseBody
	public String parentDir(String currDir) {

		try {
			currDir = java.net.URLDecoder.decode(currDir, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String parent = FileUtil.file(currDir).getParent();

		return parent;
	}


	@RequestMapping("/async")
	@ResponseBody
	public String async(String callback, FileTree tree) {

		List<FileTree> result = new ArrayList<>();

		try {
			String id = tree.getId();
			Integer level = tree.getLevel();
//			log.info("节点 id : {}，节点层级：{}。",new Object[]{id,level});

			if (HelpMe.isNull(id)) {
				File[] fileListTemp = File.listRoots();//系统根目录

				ArrayList<File> fileList = CollUtil.toList(fileListTemp);

				for (int i = 0; i < fileList.size(); i++) {
					FileTree head = new FileTree();
					head.setName(fileList.get(i).toString());
					head.setId(fileList.get(i).toString());
					head.setIsParent(true);
					head.setLastModifiedTime(DateUtil.format(FileUtil.lastModifiedTime(fileList.get(i)), HelpMe.yyyy_MM_dd_HH_mm_ss));
					head.setSize(0L);
					head.setSizeStr("暂未计算");
					result.add(head);
				}
			} else {
				File[] fileArr = FileUtil.ls(id);

				if (HelpMe.isNotNull(fileArr)) {
					for (File file : fileArr) {
						FileTree tmp = new FileTree();
						if (tree.getIsParent() != null && tree.getIsParent()) {
							try {
								tmp.setLastModifiedTime(DateUtil.format(FileUtil.lastModifiedTime(file), HelpMe.yyyy_MM_dd_HH_mm_ss));

								if (file.isDirectory()) {
									//如果目录层级很深，计算目录大小会卡死，目录大小直接设置为0
									tmp.setSize(0L);
									tmp.setSizeStr("暂未计算");
								} else {
									long size = FileUtil.size(file);
									String sizeStr = FileUtil.readableFileSize(size);
									tmp.setSize(size);
									tmp.setSizeStr(sizeStr);
								}

							} catch (Exception e) {
							}
						}
						String extName = FileUtil.extName(file);
						if (canEditExtList.contains(extName)) {
							tmp.setCanEdit(true);
						}
						tmp.setName(file.getName());
						tmp.setId(file.getPath());
						tmp.setIsParent(file.isDirectory());
						String access = "";
						if (file.canRead()) {
							access += "r-";
						}
						if (file.canWrite()) {
							access += "w-";
						}
						if (file.canExecute()) {
							access += "x-";
						}
						tmp.setAccess(HelpMe.removeLastChar(access));

						result.add(tmp);
					}
				}
			}
		} catch (Exception e) {

		}

		String json = JSONUtil.toJsonStr(result);

		return (callback + "(" + json + ")");

	}

	public static void main(String[] args) {
		long size = FileUtil.size(new File("D:\\CiYun"));


		System.out.println(size);
	}


	@PostMapping(value = "/upload")
	@ResponseBody
	public String batchUpload(HttpServletRequest request, String currDir) throws IOException {
		if (HelpMe.isNull(currDir)) {
			log.info("当前目录为空，上传失败！");
			return "当前目录为空，上传失败！";
		}
		log.info("当前目录 --> " + currDir);
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		MultipartFile file = null;
		BufferedOutputStream out = null;
		for (int i = 0; i < files.size(); ++i) {
			file = files.get(i);
			if (!file.isEmpty()) {
				String extName = FileUtil.extName(file.getOriginalFilename());
				String path = currDir + osInfo.getFileSeparator() + file.getOriginalFilename();
				log.info("上传文件 --> " + path);
				try {
					out = new BufferedOutputStream(new FileOutputStream(new File(path)));

					InputStream in = file.getInputStream();
					IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
					IoUtil.close(in);
					IoUtil.close(out);

				} catch (Exception e) {
					out = null;
					String result = "You failed to upload " + i + " => " + e.getMessage();
					return result;
				}

				if ("zip".equalsIgnoreCase(extName)) {//上传的是压缩包，只对 zip 进行判断
					ZipUtil.unzip(path);//解压，并保存原压缩文件
				}

			} else {
				String result = "You failed to upload " + i + " because the file was empty.";
				return result;
			}
		}

		return "upload successful";
	}


	@GetMapping(value = "/download")
	public void download(HttpServletResponse response, String dirId) throws Exception {
		try {
			dirId = java.net.URLDecoder.decode(dirId, "UTF-8");
			log.info("下载文件 --> " + dirId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean del = false;
		File file = new File(dirId);

		//如果是目录，压缩后再下载
		if (file.isDirectory()) {
			file = ZipUtil.zip(file);
			del = true;
		}
		String filename = file.getName();
		filename = java.net.URLEncoder.encode(filename, "UTF-8");

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		response.setCharacterEncoding("utf-8");
		ServletOutputStream out = response.getOutputStream();

		FileInputStream in = new FileInputStream(file);
		IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
		IoUtil.close(in);
		IoUtil.close(out);

		if (del) {
			FileUtil.del(file);
		}
	}


	@GetMapping("/view")
	public String view(String dirId, Model model) {

		try {
			dirId = java.net.URLDecoder.decode(dirId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		model.addAttribute("dirId", dirId);

		String content = FileUtil.readUtf8String(dirId);

		model.addAttribute("content", content);

		return "back/file/fileView";
	}


	@PostMapping(value = "/saveContent")
	@ResponseBody
	public String saveContent(@RequestBody FileContent fileContent) {

		String dirId = fileContent.getDirId();

		String content = fileContent.getContent();

		FileUtil.writeUtf8String(content, dirId);

		return "true";
	}


	@RequestMapping("/createDir")
	@ResponseBody
	public String createDir(String currDir, String fileName, String dirId, String callback) {

		boolean result = true;
		if (HelpMe.isNull(dirId)) {//添加
			String path = currDir + osInfo.getFileSeparator() + fileName;
			log.info("创建目录 --> " + path);
			FileUtil.mkdir(path);
		} else {//修改
			String extName = FileUtil.extName(dirId);
			String newPath = "";
			if (HelpMe.isNull(extName)) {
				newPath = currDir + osInfo.getFileSeparator() + fileName;
			} else {
				newPath = currDir + osInfo.getFileSeparator() + fileName + "." + extName;
			}
			File file = new File(dirId);
			file.renameTo(new File(newPath));
			log.info("修改文件(夹)名称 {} --> {}", new Object[]{dirId, newPath});
		}


		String json = JSONUtil.toJsonStr(result + "");

		return (callback + "(" + json + ")");
	}


	@RequestMapping("/delDir")
	@ResponseBody
	public String delDir(String dirIdList, String callback) {

		List<String> idList = HelpMe.easySplit(dirIdList);

		boolean result = false;

		if (HelpMe.isNotNull(idList)) {
			for (String path : idList) {
				FileUtil.del(path);
				log.info("删除文件(夹) --> " + path);
			}
			result = true;
		}

		String json = JSONUtil.toJsonStr(result + "");

		return (callback + "(" + json + ")");
	}


	@RequestMapping("/dirSize")
	@ResponseBody
	public String dirSize(String dirId) {
		log.info("开始计算目录：{} 的大小...", dirId);

		long size = FileUtil.size(FileUtil.file(dirId));
		String sizeStr = FileUtil.readableFileSize(size);

		log.info("目录：{} 的大小为：{}", dirId, sizeStr);

		return sizeStr;
	}



	@RequestMapping("/canEdit")
	@ResponseBody
	public String test(String dirId) {

		if (canEdit(dirId)) {
			return "true";
		}

		return "false";
	}


	/**
	 * 判断目录是否可以编辑
	 *
	 * @param dir
	 */
	private boolean canEdit(String dir) {
		/*for (String rootDir : rootDirList) {
			if (dir.equals(rootDir)) {
				break;
			}
			if (dir.contains(rootDir)) {
				return true;
			}
		}*/
		return true;
	}




}






































