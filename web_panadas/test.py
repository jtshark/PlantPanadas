from llama_cpp import Llama

llm = Llama(model_path="/srv/work/QE/plant_panadas/model/plant-panadas-q5_1.bin",
            n_gpu_layers=100, n_threads=1, n_ctx=2048, n_batch=128)

res = llm.create_completion(
    "### Request: Imagen a zoo filled with magical creatures. We have unicorns, dragons, and mermaids. Each of these creatures has a name, age, and a magic power. They also perform magic tricks.\n"
    "### Expert 1: ",
    max_tokens=2048
)

print(res["choices"][0]["text"])
