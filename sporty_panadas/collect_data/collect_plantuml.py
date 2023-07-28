import os

import openai
from dotenv import load_dotenv
import time

load_dotenv("tokens.env")

openai.api_key = os.getenv("OPENAI_API_KEY")

prompt = "Create a PlantUML with a meaningful filename based on the previous step by step instruction:\n" \
         "The output should be formulated as follows:\n" \
         "Filename: [Filename].puml" \
         "@startuml\n" \
         "[Content]\n" \
         "@enduml"

if __name__ == '__main__':
    folder_path = "conversations"
    count = 0
    for filename in os.listdir(folder_path):
        path = os.path.join(folder_path, filename)
        count += 1
        if count >= 10:
            break
        if os.path.isfile(path):
            try:
                output_path = os.path.join("plantuml", filename)
                if os.path.exists(output_path):
                    continue
                with open(path, "r") as f:
                    discussion = f.read()
                    time.sleep(5)
                    completion = openai.ChatCompletion.create(
                        model="gpt-4",
                        messages=[
                            {"role": "user", "content": f"{discussion}"},
                            {"role": "system", "content": prompt},
                        ],
                        max_tokens=3000,
                        temperature=1.3,
                        top_p=1,
                        frequency_penalty=0,
                        presence_penalty=0.3,
                    )
                    result = completion["choices"][0]["message"]["content"]
                with open(output_path, "w") as f:
                    f.write(f"{discussion}\n### PlantUML:\n{result}")
            except Exception as e:
                print(e)
