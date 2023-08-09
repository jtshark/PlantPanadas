import requests
import json

if __name__ == '__main__':
    # Define the url of the flask server
    url = 'http://localhost:5000'

    # Define your data
    data = {
        'model': 'test_model',
        'messages': [
            {
                "role": "user",
                "content": "Hello, how are you?"
            }
        ],
    }

    response = requests.post(url, json=data)

    print(response.json())
