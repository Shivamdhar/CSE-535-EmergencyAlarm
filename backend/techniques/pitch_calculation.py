import crepe
import math
import numpy as np
from scipy.io import wavfile

sr, audio = wavfile.read('../recordings/firetruck.wav')
time, frequency, confidence, activation = crepe.predict(audio, sr)
print(np.mean(frequency))
pitch = 69 + 12*(math.log((np.mean(frequency)/440),2))
print("Firetruck pitch:" + str(pitch))



sr, audio = wavfile.read('../recordings/firetruck_trim.wav')
time, frequency, confidence, activation = crepe.predict(audio, sr)
print(np.mean(frequency))
pitch = 69 + 12*(math.log((np.mean(frequency)/440),2))
print("Firetruck pitch:" + str(pitch))