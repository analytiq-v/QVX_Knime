# capstone-project
There are two Java projects in this repository: QvxReader and QvxWriter.
Both of these project folders are within the folder "capstone-project/eclipse-workspace".

QvxReader: KNIME node that allows the user to select a qvx input file. On execution, this file is read by the node, and a BufferedDataTable
is created on the output port.

QvxWriter: KNIME node that allows the user to select a qvx output file. The input port accepts a BufferedDataTable, and on execution,
a qvx file is generated from this BufferedDataTable.

The other two project folders (QvxAdapter and QvxExtension) are in-progress and can be considered rough drafts at the moment. In short,
Monica and I are occasionally referencing them, and they can be ignored by anyone else (this may change in the future; this README
will be updated if the project structure changes).

Running the projects:
Before running the projects, you must set up the KNIME Analytics SDK. Instructions are found here:
https://github.com/knime/knime-sdk-setup

In Eclipse, click on "Open Projects from File System". Select either the folder "capstone-project/eclipse-workspace/QvxReader" or
"capstone-project/eclipse-worksapce/QvxWriter". It is recommended that you have both of these projects open at once, so that
both the QvxReader node and the QvxWriter node are available in KNIME when it is launched.
