# Python code that trains the fraud detection model to 0.75% of the data set

import pandas as pd
import joblib

from sklearn.ensemble import RandomForestClassifier

df = pd.read_csv('../../data/creditcard.csv')
print('The data has {df.shape[0]} rows and {df.shape[1]} columns')
#sort dataframe by Time
df.sort_values(by='Time', inplace=True)

#number of rows in the dataset
n_samples = df.shape[0]

train_size = 0.75

n_train = int(train_size * n_samples)

df_train = df.iloc[0:n_train] #first n_train rows are train
df_test = df.iloc[n_train:] #rest is test

features_train = df_train.drop(['Time', 'Class','V1','V2','V5','V6','V7','V8','V9','V13','V15','V16','V18','V19','V20','V21','V22','V23','V24','V25','V26','V27','V28'], axis=1)
target_train = df_train['Class']

features_test = df_test.drop(['Time', 'Class','V1','V2','V5','V6','V7','V8','V9','V13','V15','V16','V18','V19','V20','V21','V22','V23','V24','V25','V26','V27','V28'], axis=1)
target_test = df_test['Class']

print('The trained data has {features_train.shape[0]} rows and {features_train.shape[1]} columns')

print(features_train[:5])

model = RandomForestClassifier(n_estimators=200, 
                               max_depth=6, 
                               n_jobs=10,
                               class_weight='balanced')
model.fit(features_train, target_train)

#save mode in filesystem
joblib.dump(model, 'modelfull.pkl') 
