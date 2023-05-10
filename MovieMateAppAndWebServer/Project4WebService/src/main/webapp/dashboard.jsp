<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.bson.Document" %>
<%@ page import="java.util.List" %>
<!-- Author : Praveen Ramesh -->
<!-- Andrew ID: pramesh2@andrew.cmu.edu -->
<!-- JSP page to display MovieMate operational analytics and logs -->
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
<!-- Page heading -->
<h1>MovieMate Dashboard</h1>
<!-- Buttons to toggle between operational analytics and logs -->
<button onclick="showMovieMateOperationalAnalytics()">Operational Analytics</button>
<button onclick="showMovieMateLogs()">Logs</button>
<!-- Section to display operational analytics -->
<div id="operationalAnalytics">
    <h2>MovieMate Operational Analytics</h2>
    <!-- Display average latency  -->
    <p>Average latency: <%= request.getAttribute("averageLatency") %> ms</p>
    <!-- Display top searched movie  -->
    <p>Top searched movie: <%= request.getAttribute("topSearchMovie") %></p>
    <!-- Display top suggestions asked  -->
    <p>Top suggested genre: <%= request.getAttribute("topSuggestionsAsked") %></p>
</div>

<!-- Section to display logs, hidden by default -->
<div id="logs" style="display:none;">
    <h2>MovieMate Logs</h2>
    <!-- Table to display logs -->
    <table>
        <tr>
            <!-- Column headings -->
            <th>Event Type</th>
            <th>Request Element</th>
            <th>Request Timestamp</th>
            <th>Mobile Model</th>
            <th>Latency</th>
            <th>Title</th>
            <th>Poster Path</th>
            <th>Vote Average</th>
            <th>Release Date</th>
            <th>Overview</th>
        </tr>
        <!-- Loops over all the documents in the List -->
        <% List<Document> logs = (List<Document>) request.getAttribute("logs");
            for (Document log : logs) { %>
        <tr>
            <!-- Display log data -->
            <td><%= log.getString("eventType") %></td>
            <td><%= log.getString("requestElement") %></td>
            <td><%= log.getString("requestTimestamp") %></td>
            <td><%= log.getString("mobileModel") %></td>
            <td><%= log.getLong("latency") %></td>
            <td><%= log.getString("title") %></td>
            <td><%= log.getString("poster_path") %></td>
            <td><%= log.getString("vote_average") %></td>
            <td><%= log.getString("release_date") %></td>
            <td><%= log.getString("overview") %></td>
        </tr>
        <% } %>
    </table>
</div>

<!-- JavaScript functions to toggle between displaying operational analytics and logs of MovieMate -->
<script>
    function showMovieMateOperationalAnalytics() {
        document.getElementById("operationalAnalytics").style.display = "block";
        document.getElementById("logs").style.display = "none";
    }

    function showMovieMateLogs() {
        document.getElementById("operationalAnalytics").style.display = "none";
        document.getElementById("logs").style.display = "block";
    }
</script>
</body>
</html>
