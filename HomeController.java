package excelbb.controller;
import excelbb.ClipBoard;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
@Controller
public class HomeController {

	public static String excelPath = "abc";
	public static String cgkeyPath = "D:\\projects\\dotnetprojects\\SendKeys-master\\SendKeys-master\\SendKeys\\bin\\Debug\\cgkey.exe";

	@RequestMapping(path="/",method= RequestMethod.GET)
	public String home(){
		return "index";
	}

	@RequestMapping(value="/query",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String query(@RequestBody String s)
			throws IOException, JSONException {
		JSONObject jsonObject = new JSONObject(s);
		String content = (String) jsonObject.get("content");
		return new Date().toString();
	}

	@RequestMapping(value="/dump",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String dump(@RequestBody String s)
			throws IOException, JSONException {
		JSONObject jsonObject = new JSONObject(s);
		String content = (String) jsonObject.get("content");
		ClipBoard.setStringUtilSuccess(content);
		sendKeys("idea","^(v)");
		return "ok";
	}

	public boolean sendKeys(String processNameContain, String keys)
			throws IOException {
		boolean b = false;
		keys = "\""+ keys +"\"";
		Process proc = Runtime.getRuntime().exec(String.format("%s %s %s",cgkeyPath , processNameContain, keys));
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new
				InputStreamReader(proc.getErrorStream()));
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			if("0".equalsIgnoreCase(s)){
				return true;
			}else{
				return false;
			}
		}
		while ((s = stdError.readLine()) != null) {
			if("0".equalsIgnoreCase(s)){
				return true;
			}else{
				return false;
			}
		}
		return b;
	}
}
