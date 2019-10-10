import librosa.display

data, sampling_rate = librosa.load('../recordings/templebell.wav')
X = librosa.stft(data)
Xdb = librosa.amplitude_to_db(abs(X))
print("Intensity: " + str(Xdb))

librosa.display.specshow(Xdb, sr=sampling_rate, x_axis='time', y_axis='hz')
plt.figure(figsize=(14, 5))
plt.colorbar()
plt.show()