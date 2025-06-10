package com.nt.servlet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nt.object.PostObject;
@WebServlet("/FetchFilteredDataServlet")
public class FetchFilteredDataServlet extends HttpServlet {
	private static final String API_URL="https://mocki.io/v1/391ed881-ac00-44dc-92b5-f2757af232d7";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter out= res.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Filtered Posts</title>");
		out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/css/bootstrap.min.css\' rel=\'stylesheet\' integrity=\'sha384-4Q6Gf2aSP4eDXB8Miphtr37CMZZQ5oXLH2yaXMJ2w8e2ZtHTl7GptT4jmndRuHDT\' crossorigin=\'anonymous'>");
		out.println("</head>");
		out.println("<body class='bg-light'>");

		out.println("<div class='container mt-5'>");
		String startDateStr = req.getParameter("startDate");
		String endDateStr = req.getParameter("endDate");
		
		if(startDateStr == null || endDateStr == null) {
			out.println("<h3>Error: Missing date parameters.</h3>");
			return;
		}
		
		try {
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(endDateStr);
			
			List<PostObject> allPosts = fetchPostObjectFromAPI();
			List<PostObject> filteredPosts = new ArrayList<>();
			
			for(PostObject post : allPosts) {
				Date postDate = sdf.parse(post.getDate());
				if(!postDate.before(startDate) && !postDate.after(endDate)) {
					filteredPosts.add(post);
				}
			}
			if(filteredPosts.isEmpty()) {
				out.println("<h2>No posts found in the given date range<h2>");
				out.println("<a href='index.html' class='btn btn-primary mt-3'>Go To Home</a>");
			}
			else {
			out.println("<h2>Filtered Posts</h2>");
			out.println("<table class='table table-success table-striped-columns'>");
			out.println("<tr><th>ID</th><th>Title</th><th>Description</th><th>Date</th><tr>");
			
			for(PostObject post : filteredPosts) {
				out.println("<tr>");
				out.println("<td>"+post.getId()+"</td>");
				out.println("<td>"+post.getTitle()+"</td>");
				out.println("<td>"+post.getDescription()+"</td>");
				out.println("<td>"+post.getDate()+"</td>");
				out.println("</tr>");	
				
			}
			out.println("</table>");
			out.println("<a href='index.html' class='btn btn-primary mt-3'>Home</a>");
			
			}
			out.println("</div>");

			out.println("</body>");
			out.println("</html>");
		}catch (Exception e) {
			out.println("<h2>Error processing request: " + e.getMessage() + "</h3>");
			e.printStackTrace();
		}
	}
	private List<PostObject> fetchPostObjectFromAPI() throws IOException{
		URL url = new URL(API_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		List<PostObject> posts = new Gson().fromJson(reader, new TypeToken<List<PostObject>>() {}.getType());
		reader.close();
		
		//Prefix IDs with "NDSS"
		for(PostObject post :posts) {
			post.setId(post.getId());
		}
		
		return posts;
	}
}
