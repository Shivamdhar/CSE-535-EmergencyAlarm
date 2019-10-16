import math
import numpy
from scipy.io import wavfile

fs, data = wavfile.read('../recordings/bike.wav')
blockLinearRms = numpy.sqrt(numpy.mean(data**2))
print(blockLinearRms) #amplitude
blockLogRms = 20 * math.log10(blockLinearRms)
print(blockLogRms) #loudness