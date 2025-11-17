import json
import requests
import matplotlib.pyplot as plt
from datetime import datetime

API_URL = "http://localhost:8080/api/chat/sentiment"
JSON_FILE = "chat_data.json"   # Your saved conversation file

def load_chat_data(filename):
    with open(filename, "r", encoding="utf-8") as file:
        return json.load(file)

def send_request(chat_json):
    headers = {"Content-Type": "application/json; charset=UTF-8"}
    response = requests.post(API_URL, headers=headers, json=chat_json)

    if response.status_code != 200:
        print(f"Error {response.status_code}: {response.text}")
        return None
    print(response.json)
    return response

def plot_sentiment(sentiment_data):
    timestamps = []
    values = []

    for entry in sentiment_data["sentiments"]:
        timestamps.append(datetime.fromiso8601(entry["timestamp"].replace("Z","")))
        values.append(float(entry["sentiment"]))

    plt.figure(figsize=(10, 5))
    plt.plot(timestamps, values, marker='o')
    plt.title("Sentiment Progression Over Time")
    plt.xlabel("Time")
    plt.ylabel("Sentiment Score")
    plt.grid(True)
    plt.tight_layout()
    plt.show()

def main():
    chat_json = load_chat_data(JSON_FILE)
    # print(chat_json)
    result = send_request(chat_json)
    print(result)
    if result:
        print("Sentiment data received. Plotting...")
        # plot_sentiment(result)
    else:
        print("Could not gather sentiment data.")

if __name__ == "__main__":
    main()
