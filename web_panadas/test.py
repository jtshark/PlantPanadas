from llama_cpp import Llama

llm = Llama(model_path="./model/plant-panadas-llm-q4_1.bin",
            n_gpu_layers=100, n_threads=8, n_ctx=4096, n_batch=1024)

print("start", flush=True)
request = "### Request: A drug and weapon shop where users can buy things in the darknet.\n### Expert 1: "

res = llm.create_completion(
    request,
    max_tokens=4096,
    temperature=0.5
)

request += res["choices"][0]["text"]
print(request)
