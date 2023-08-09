from huggingface_hub import hf_hub_download
from llama_cpp import Llama

llama_path = hf_hub_download(repo_id="TheBloke/Llama-2-13B-GGML", filename="llama-2-13b.ggmlv3.q4_0.bin")

llm = Llama(model_path="/srv/work/QE/plant_panadas/model/plant-panadas-q4_0.bin",
            n_gpu_layers=100, n_threads=8, n_ctx=2048, n_batch=1024)

print("start", flush=True)
request = "### Request: Imagen a zoo filled with magical creatures. We have unicorns, dragons, and mermaids. Each of these creatures has a name, age, and a magic power. They also perform magic tricks.\n### Expert 1: "

res = llm.create_completion(
    request,
    max_tokens=2048
)

request += res["choices"][0]["text"]
print(request)
