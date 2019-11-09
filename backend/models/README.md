`fetaure_extraction.py`: contains code for extracting features from the audio dataset

`original.csv`: contains the original features extracted from the audio dataset

`datsetfold1.csv`: replaces all other labels with 0, except 8 which is replaced by 1. Label 8 stands for alarm in the sound dataset.

`model.py`: contains decision tree classifier used to build the sound model

`tester.py`: contains test code till app integration is complete

Following is an example of decision tree created for `datsetfold1.csv`:

![Decision tree example on the dataset](alarm.png)
