import crepe
import librosa.display
import math
import numpy
import os
from pylab import *
from scipy.io import wavfile

class Tester():
    def __init__(self):
        self.filename = "firetruck2.wav"

    def fetch_test_data(self):
        fs, data = wavfile.read(self.filename)
        blockLinearRms = numpy.sqrt(numpy.mean(data**2)) #amplitude
        blockLogRms = 20 * math.log10(blockLinearRms) #loudness

        data = data / (2.**15)
        if len(data.shape) == 1:
            s1 = data
        else:
            s1 = data[:,0]

        rms_val = sqrt(mean(s1**2)) #rms

        time, frequency, confidence, activation = crepe.predict(data, fs)
        pitch = 69 + 12*(math.log((numpy.mean(frequency)/440),2)) #pitch

        if blockLinearRms == numpy.nan:
            blockLinearRms = 0
        if blockLogRms == numpy.nan:
            blockLogRms = 0
        if rms_val == numpy.nan:
            rms_val = 0
        if pitch == numpy.nan:
            pitch = 0

        return [str(blockLinearRms),str(blockLogRms),str(rms_val),str(pitch)]