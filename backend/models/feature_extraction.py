"""
To be run on the audio dataset to extract features - Amplitude, Loudness, RMS, Pitch
"""
import crepe
import librosa.display
import math
import numpy
import os
from pylab import *
from scipy.io import wavfile

files = os.listdir("fold1")
output_file = open("/Users/dv/Downloads/UrbanSound8K/audio/fold1new/datsetfold1.csv", "a")
output_file.write("Amplitude,Loudness,RMS,Pitch,Label\n")
base_path = "/Users/dv/Downloads/UrbanSound8K/audio/fold1new/"
files.sort()

for file in files[300:350]:
	if file.endswith(".wav"):
		try:
			fs, data = wavfile.read(base_path + file)
			blockLinearRms = numpy.sqrt(numpy.mean(data**2)) #amplitude
			blockLogRms = 20 * math.log10(blockLinearRms) #loudness

			data = data / (2.**15)
			if len(data.shape) == 1:
				s1 = data
			else:
				s1 = data[:,0]

			rms_val = sqrt(mean(s1**2)) #rms

			time, frequency, confidence, activation = crepe.predict(data, fs)
			pitch = 69 + 12*(math.log((np.mean(frequency)/440),2)) #pitch

			class_label = file.split("-")[1]

			output_file.write(str(blockLinearRms) + "," + str(blockLogRms) + "," + str(rms_val) + "," + str(pitch) + "," + str(class_label) + "\n")
		except Exception as e:
			print(e)