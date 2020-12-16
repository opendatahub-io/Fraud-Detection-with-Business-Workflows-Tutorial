#! /usr/bin/env python3

import argparse
from kafka import KafkaConsumer
import requests
import time
import csv
import os

def getToken(seldon):
    post_data = {"grant_type": "client_credentials"}
    requestOauth = requests.post(seldon+'/oauth/token', auth=('oauth-key', 'oauth-secret'), data=post_data, json={'grant_type=client_credentials'})
    # print(requestOauth.content)
    data = requestOauth.json();
    access_token = data['access_token']
    return access_token

def invokeModel(msg, access_token, seldon):

# "Time","V1","V2","V3","V4","V5","V6","V7","V8","V9","V10","V11","V12","V13","V14","V15","V16","V17","V18","V19","V20","V21","V22","V23","V24","V25","V26","V27","V28","Amount","Class" - Fields we get on the consumer
# ['V3','V4','V10','V11','V12','V14','V17','Amount'] - Fields to be sent to the model

    payload = ""+msg[3]+","+msg[4]+","+msg[10]+","+msg[11]+","+msg[12]+","+msg[14]+","+msg[17]+","+msg[29]
    headers = {'Content-type': 'application/json', 'Authorization': 'Bearer {}'.format(access_token)}
    #Read the test dataframe and stream each row

    # Send the post request for the prediction
    requestPrediction = requests.post(seldon+'/api/v0.1/predictions', headers=headers, json={"strData": payload })
    predictionData = requestPrediction.json();
    datafield = predictionData['data']
    predictionArray = datafield['ndarray']
    print(predictionArray[0])

def main():

    consumer_group = os.environ['consumergroup']
    seldon = os.environ['seldon']
    topic = os.environ['topic']
    bootstrap = os.environ['bootstrap']

    access_token = getToken(seldon)
    print(access_token)
    consumer = KafkaConsumer(topic,
                             group_id=consumer_group,
                             auto_offset_reset='earliest',
                             bootstrap_servers=bootstrap)

    print("Subscribed to {} on topic '{}'...".format(bootstrap, topic))

    # Even though this looks like a determinate for loop, this actually runs
    # until interrupted (even if there are no messages are left to consume)
    try:
        for record in consumer:
            msg = record.value.decode('utf-8')
            msgAsList = msg[1:-1].split(",")
            modelResponse = invokeModel(msgAsList, access_token, seldon)

    except KeyboardInterrupt:
        pass
    finally:
        # Don't forget to clean up after yourself!
        print("Closing KafkaConsumer...")
        consumer.close()
    print("Done.")


# For more documentation on Consumer: https://pypi.org/project/kafka-python/

if __name__ == '__main__':
    main()


# ### Testing the served model from python using the test dataframe
