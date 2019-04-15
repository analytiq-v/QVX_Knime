# capstone-project
There are two Java projects in this repository: QvxExtensionFinal and QvxWriter.
Both of these project folders are within the folder "capstone-project/eclipse-workspace".

QvxExtensionFinal (QvxReader): KNIME node that allows the user to select a qvx input file. On execution, a corresponding csv is read by the node, and a BufferedDataTable
is created on the output port.

QvxWriter: KNIME node that allows the user to select a qvx output file. The input port accepts a BufferedDataTable, and on execution,
a qvx file is generated from this BufferedDataTable.

Running the projects:
Before running the projects, you must set up the KNIME Analytics SDK. Instructions are found here:
https://github.com/knime/knime-sdk-setup

In Eclipse, click on "Open Projects from File System". Select either the folder "capstone-project/eclipse-workspace/QvxExtensionFinal" or
"capstone-project/eclipse-worksapce/QvxWriter". It is recommended that you have both of these projects open at once, so that
both the Qvx Reader node and the QvxWriter node are available in KNIME when it is launched.
