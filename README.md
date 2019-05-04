# capstone-project
There are two main Java projects in this repository: "QvxReader" and "QvxWriter".
Both of these project folders are within the folder "capstone-project/eclipse-workspace".

QvxReader: KNIME node that allows the user to select a qvx input file. On execution, this file is read by the node, and a BufferedDataTable is created on the output port.

QvxWriter: KNIME node that allows the user to select a qvx output file. The input port accepts a BufferedDataTable, and on execution, a qvx file is generated from this BufferedDataTable.

Running the project in KNIME:
Copy the 2 jar files from the "plugins" folder into  the "plugins" directory of the KNIME installation. Note: This project is compatible with KNIME 3.7.x.

Running the projects in Eclipse:
Before running the projects, you must set up the KNIME Analytics SDK. Instructions are found here:
https://github.com/knime/knime-sdk-setup

In Eclipse, click on "Open Projects from File System". Select either the folder "capstone-project/eclipse-workspace/Reader" or "capstone-project/eclipse-worksapce/QvxWriter". It is recommended that you have both of these projects open at once, so that both the Qvx Reader node and the Qvx Writer node are available in KNIME when it is launched.

There is another project in the "capstone-project/eclipse-workspace" folder, called "JAXB Generation". We used JAXB to generate Java classes to help us deal with the XML-formatted QVX Table Header. We have already copied these generated classes into both the QvxReader and the QvxWriter projects. Thus, it is not necessary to run "JAXB Generation" in order to test our QvxReader and QvxWriter.
