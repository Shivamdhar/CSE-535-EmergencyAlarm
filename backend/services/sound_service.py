from backend.models.model import Model
from os import path
from pydub import AudioSegment

class SoundService():
    def __init__(self):
        self.model = Model()
        self.base  = "/Users/shivam-dhar/Downloads/CSE-535-EmergencyAlarm-master/backend/test/"

        self.logger = open("../logs/logger.log", "a")

    def analyze(self, filename):
        wavfile = self.convert_3gp_to_wav(filename)
        self.model.train()
        label = self.model.test(wavfile)

        if str(label[1]) == "0":
            return False
        else:
            return True

    def train(self):
        return self.model.train()

    def convert_3gp_to_wav(self, sourcefile):
        destinationfilemp3 = sourcefile.split(".")[0] + ".mp3"
        destinationfile = sourcefile.split(".")[0] + ".wav"
        try:
            sound = AudioSegment.from_file(sourcefile)
            sound.export(destinationfilemp3, format="mp3")
           
            sound = AudioSegment.from_mp3(destinationfilemp3)
            sound.export(destinationfile, format="wav")
        except:
            return destinationfile
        return destinationfile