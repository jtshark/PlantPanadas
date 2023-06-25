from flask import Flask, request, jsonify
from huggingface_hub import hf_hub_download
from llama_cpp import Llama

app = Flask(__name__)

llama_path = hf_hub_download(repo_id="TheBloke/wizard-mega-13B-GGML", filename="wizard-mega-13B.ggmlv3.q4_1.bin")
llm = Llama(model_path=llama_path, n_gpu_layers=60, n_threads=1, n_ctx=2048)


def llama_model(messages):
    prompt = ""
    for message in messages:

        if message["role"] == "user":
            role_name = "USER"
        elif message["role"] == "assistant":
            role_name = "Assistant"
        elif message["role"] == "system":
            role_name = "Instruction"
        else:
            role_name = "Unknown"

        prompt += f"### {role_name}: {message['content']}\n"
    prompt += "### Assistant:\n"
    output = llm(prompt, max_tokens=2048, temperature=0)
    return output["choices"][0]["text"]


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
    app.run()
