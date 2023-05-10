# Project Name: Crime Analytics using Hadoop Mapper-Reducer

## Description
The Hadoop Crime Mapper-Reducer is a Java-based project that leverages Hadoop MapReduce to process and analyze crime data, generating a KML file to visualize aggravated assault incidents within a specific area. The project focuses on crime data from January 1990 through December 1999 for serious violent crimes (FBI Part 1) in Pittsburgh.

The input file, `CrimeLatLonXYTabs.txt`, is a tab-delimited text file containing individual criminal offense incidents, including their State Plane (X, Y) coordinates (in feet), time, street address, type of offense, date, 2000 census tract, latitude, and longitude. This data can be utilized in GIS tools, such as Google Earth Pro, for visualization purposes.

The Hadoop Crime Mapper-Reducer project filters aggravated assault crimes occurring within a 350-meter radius of a target location in Oakland, Pittsburgh. The Pythagorean theorem is employed to determine whether a particular aggravated assault occurred within 350 meters of the target location.

The project's output is a well-formed KML file suitable for viewing in Google Earth, allowing users to visualize the selected crimes on a map. The MapReduce framework efficiently processes the input data and generates the desired KML output.
![Example Image](https://github.com/Praveenramesh0508/CMUProjects/blob/main/CrimeAnalyticsHadoop/img.png)
