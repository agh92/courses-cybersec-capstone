package org.coursera.cybersecurity.capstone.group8.webapi;

import java.sql.SQLException;

import org.coursera.cybersecurity.capstone.group8.internal.data.DbTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbDumpController {
	private Logger log = LoggerFactory.getLogger(DbDumpController.class);

	@Autowired
	private DbTools dbtools;

	@RequestMapping(path="/dbdump", method=RequestMethod.GET, produces=MediaType.TEXT_PLAIN_VALUE)
	public String dbdump() throws SQLException {
		log.info("Doing database dump");
		return dbtools.backup();
	}
}
