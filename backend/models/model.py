# Model contains methods - train and test
# Decision tree classifier
import numpy as np
import pandas as pd

from sklearn import metrics
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier

from tester import Tester

class Model():  
    def __init__(self):
        self.col_names = ["Amplitude", "Loudness", "RMS", "Pitch", "Label"]
        self.features = ["Amplitude", "Loudness", "RMS", "Pitch"]
        self.clf = None
        self.model_base = "/Users/shivam-dhar/Downloads/CSE-535-EmergencyAlarm-master/backend/models/"
        self.test_base = "/Users/shivam-dhar/Downloads/CSE-535-EmergencyAlarm-master/backend/test/"
        self.tester = Tester()

    def train(self):
        alarm = pd.read_csv(self.model_base + "datsetfold1.csv", header=None, names=self.col_names)
        X = alarm[self.features]
        y = alarm.Label
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0, random_state=1)

        self.clf = DecisionTreeClassifier()
        self.clf = self.clf.fit(X_train,y_train)

        return "Training done"

    def test(self, filename):
        data = self.tester.fetch_test_data(filename)
        return str(self.clf.predict(np.array(data).reshape(1,-1)))