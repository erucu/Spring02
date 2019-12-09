package com.wildcodeschool.wizardSite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.sql.*;

@Controller
@SpringBootApplication
public class WizardSiteApplication {

	// Save the database connection
	static Connection con;

	public static void main(String[] args) throws Exception {
		// Connect to the database
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wild_db_quest", "root", "passwort");
		SpringApplication.run(WizardSiteApplication.class, args);
	}

	@RequestMapping("/")
	@ResponseBody
	public String index() throws Exception {
		// Run an SQL query and save the result in 'rs'
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from team");

		// Start writing the HTML
		String output = "<h1>List of teams</h1><ul>";

		// Iterate through the results.
		while (rs.next()) {
			// Add a team as a list item
			output += "<li><a href=team/" + rs.getInt("id") + ">" + rs.getString("name") + "</a></li>";
		}
		output += "</ul>";
		return output;
	}

	@RequestMapping("/team/{team_id}")
	@ResponseBody
	public String hello(@PathVariable int team_id) throws Exception {
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select * from team where id = " + team_id);
		// If there is such a team, give the players
		if (rs.next()) {
			String output = "<h1>" + rs.getString("name") + "</h1><ul>";
			Statement stmt2 = con.createStatement();
			ResultSet rs2 = stmt2.executeQuery(
					"select firstname, lastname from wizard join player on wizard.id = player.wizard_id where player.team_id = "
							+ team_id);
			while (rs2.next()) {
				// Add a team as a list item
				output += "<li>" + rs2.getString("firstname") + " " + rs2.getString("lastname") + "</li>";
			}
			output += "</ul>";
			return output;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such team!");
		}

	}
}
