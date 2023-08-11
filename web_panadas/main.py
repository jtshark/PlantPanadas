from flask import Flask, request, jsonify
from huggingface_hub import hf_hub_download
from llama_cpp import Llama

app = Flask(__name__)

llm = Llama(model_path="./model/plant-panadas-llm-q4_1.bin",
            n_gpu_layers=60, n_threads=8, n_ctx=4096, n_batch=128)


def llama_model(messages):
    prompt = ""
    for message in messages:

        if message["role"] == "user":
            role_name = "Answer"
        elif message["role"] == "assistant":
            role_name = ""
        elif message["role"] == "system":
            role_name = "Request"
        else:
            role_name = "Unknown"

        prompt += f"### {role_name}: {message['content']}\n"
    prompt += "### Expert 1:"

    print(prompt)
    output = llm(prompt, max_tokens=4096, temperature=0, stop=["### Answer:"])
    return "### Expert 1:" + output["choices"][0]["text"]


@app.route('/', methods=['POST'])
def get_answer():
    data = request.get_json()

    print(data)
    messages = data["messages"]

    return jsonify({
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": llama_model(messages)
                }
            }
        ]
    })


if __name__ == '__main__':
    app.run(port=5632)
