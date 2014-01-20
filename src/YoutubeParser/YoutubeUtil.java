package YoutubeParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeUtil
{
	public static List<Video> getLinks(String youtubeLink) throws Throwable
	{
		URL website = new URL(youtubeLink);
		InputStream inputStream = website.openStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder contentBuffer = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null)
		{
			contentBuffer.append(line.replace("\\u0026", "&"));
		}

		String websiteContent = contentBuffer.toString();

		List<String> matches = new ArrayList<String>();
		Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
		Matcher matcher = p.matcher(websiteContent);
		while (matcher.find())
		{
			matches.add(matcher.group());
		}
		Map<String, String> videoUrls = new HashMap<String, String>();

		for (String currentString : matches.get(0).split(","))
		{
			String url = URLDecoder.decode(currentString, "UTF-8");

			Pattern patternITag = Pattern.compile("itag=([0-9]+?)[&]");
			Matcher matcherITag = patternITag.matcher(url);
			String itag = null;
			if (matcherITag.find())
				itag = matcherITag.group(1);

			Pattern patternSig = Pattern.compile("sig=(.*?)[&]");
			Matcher matcherSig = patternSig.matcher(url);
			String sig = null;
			if (matcherSig.find())
				sig = matcherSig.group(1);

			Pattern patternUrl = Pattern.compile("url=(.*?)[&]");
			Matcher matcherUrl = patternUrl.matcher(currentString);
			String um = null;
			if (matcherUrl.find())
				um = matcherUrl.group(1);

			if (itag != null && sig != null && um != null)
				videoUrls.put(itag, URLDecoder.decode(um, "UTF-8") + "&" + "signature=" + sig);
		}

		HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
		typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
		typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
		typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
		typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
		typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
		typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
		typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
		typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
		typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
		typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
		typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
		typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
		typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
		typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

		List<Video> videos = new ArrayList<Video>();
		for (String format : typeMap.keySet())
		{
			Meta meta = typeMap.get(format);
			if (videoUrls.containsKey(format))
			{
				videos.add(new Video(meta, videoUrls.get(format)));
			}
		}
		return videos;
	}

	static class Meta
	{
		public String num;
		public String type;
		public String ext;

		Meta(String num, String ext, String type)
		{
			this.num = num;
			this.ext = ext;
			this.type = type;
		}
	}

	static class Video
	{
		public Meta meta;
		public String url;

		Video(Meta meta, String url)
		{
			this.meta = meta;
			this.url = url;
		}
	}
}
