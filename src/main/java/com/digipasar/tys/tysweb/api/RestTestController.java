/**
 * 
 */
package com.digipasar.tys.tysweb.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digipasar.tys.tysweb.util.AExcelParser;
import com.digipasar.tys.tysweb.util.DBConnector;

/**
 * @author Arun
 *
 */
@RestController
public class RestTestController {

	@GetMapping("/hello")
	public String sayHello() {
		
		
		List<Map<String,String>> dataMap = AExcelParser.getExcelData(getClass().getResourceAsStream("TYS Data Template.xlsx"),"Space"); 
		System.out.println("Excel Value:"+dataMap);
		DBConnector.writeToTable("Space", dataMap);
		/*try {
			System.out.println(AExcelParser.getExcelData(getClass().getResource("file:D:\\TYS Data Template.xlsx").openStream(),"Space"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		return "Hello Spring Boot";
	}
}
