GLIMMPSE (General Linear Multivariate Model Power and Sample size)
Copyright (C) 2010 Regents of the University of Colorado.  

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 
------------------------------
1. INTRODUCTION
------------------------------

The GlimmpseWeb project is the main web-based interface for the
Glimmpse software system (http://glimmpse.samplesizeshop.com/).

The power calculations are based on the work of Professor Keith E. Muller
and colleagues.  A full list of related publications are available at:

http://samplesizeshop.com/education/related-publications/

------------------------------
2.  LATEST VERSION
------------------------------

Version 2.0.0

------------------------------
3.  DOCUMENTATION
------------------------------

Documentation is available from the project web site:

http://samplesizeshop.com/documentation/glimmpse/

------------------------------
4. DEPENDENCIES
------------------------------

The interface has been tested in Apache Httpd 2.2.x

== University of Colorado Denver dependencies ==
Web Service Common library 1.0.0

==Third-party dependencies==

Google Web Toolkit 2.4.0 
Google Visualization API 1.1 
Piriti 0.8

------------------------------
5.  SUPPORT
------------------------------

The GlimmpseWeb project is provided without warranty.

For questions regarding the project, please email sarah.kreidler@ucdenver.edu

------------------------------
6.  ANT BUILD SCRIPT
------------------------------

The main build.xml script is located in the ${GLIMMPSE_WEB_HOME}/build
directory.  To compile the application, change to the ${GLIMMPSE_WEB_HOME}/build
directory and type

ant

A binary, source, and javadoc distribution will be created in 

${GLIMMPSE_WEB_HOME}/build/artifacts/

The build script assumes that the directories WebServiceCommon 
and thirdparty are installed at the same directory level as 
${GLIMMPSE_WEB_HOME}. Distributions for these libraries are available at

http://samplesizeshop.com/software-downloads/glimmpse-software-downloads/

To build WebServiceCommon, run the ant script found in 
${WEB_SERVICE_COMMON_HOME}/build

------------------------------
7. CONTRIBUTORS / ACKNOWLEDGEMENTS
------------------------------

The GlimmpseWeb project was created by Dr. Sarah Kreidler and Dr. Deb Glueck
at the University of Colorado Denver, Department of Biostatistics and Informatics.

Special thanks to the following individuals were instrumental in completion of this project:
Professor Keith E. Muller
Dr. Anis Karimpour-Fard
Dr. Jackie Johnson
Uttara Sakhadeo
Vijay Akula
Brandy Ringham
Yi Guo
Zacc Coker-Dukowitz


