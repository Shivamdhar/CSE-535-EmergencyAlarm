import librosa.display
import numpy as np

X, sample_rate = librosa.load('../recordings/bike.wav', res_type='kaiser_fast')
mfccs = np.mean(librosa.feature.mfcc(y=X, sr=sample_rate, n_mfcc=40).T,axis=0)
print(mfccs)