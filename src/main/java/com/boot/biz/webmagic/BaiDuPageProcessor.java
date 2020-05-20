package com.boot.biz.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * @author mengdexuan on 2020/5/17 8:37.
 */
public class BaiDuPageProcessor implements PageProcessor {

	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

	@Override
	// process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
	public void process(Page page) {

		Selectable result = page.getHtml().xpath("//div[@id='content_left']/div/h3[@class='t']/a/@href");

		List<String> list = result.all();

		for (String str : list) {
			if (str.startsWith("http")) {
				System.out.println(str);
				System.out.println();
			}
		}


		System.out.println("结果条数：" + list.size());
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {

		String word = "JAVA";
		String url = "http://www.baidu.com.cn/s?wd=" + word + "&pn=0";

		Request request = new Request(url);

		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.addHeader("Accept-Encoding", "gzip, deflate, compress");
		request.addHeader("Accept-Language", "en-us;q=0.5,en;q=0.3");
		request.addHeader("Cache-Control", "max-age=0");
		request.addHeader("Connection", "keep-alive");
		request.addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:22.0) Gecko/20100101 Firefox/22.0");

		Spider.create(new BaiDuPageProcessor())
				//开启5个线程抓取
				.thread(5)
				.addRequest(request)
				//启动爬虫
				.run();

	}


}
