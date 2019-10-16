import matplotlib.pyplot as plt
import numpy
from pylab import *
from scipy.io import wavfile

sampFreq, snd = wavfile.read('../recordings/firetruck.wav')
snd = snd / (2.**15)

if len(snd.shape) == 1:
	s1 = snd
else:
	s1 = snd[:,0]

timeArray = arange(0, snd.shape[0], 1)
timeArray = timeArray / sampFreq
timeArray = timeArray * 1000

n = len(s1) 
p = fft(s1)
nUniquePts = int(ceil((n+1)/2.0))
p = p[0:nUniquePts]
p = abs(p)
p = p / float(n)
p = p**2

if n % 2 > 0:
    p[1:len(p)] = p[1:len(p)] * 2
else:
    p[1:len(p) -1] = p[1:len(p) - 1] * 2

freqArray = arange(0, nUniquePts, 1.0) * (sampFreq / n)
rms_val = sqrt(mean(s1**2))
loudness = sqrt(sum(p))
print(loudness)

# plt.plot(timeArray, s1, color='k')
# ylabel('Amplitude')
# xlabel('Time (ms)')
# plt.show()