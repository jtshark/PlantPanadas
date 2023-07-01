import os
import time
from typing import List

import openai
from dotenv import load_dotenv

load_dotenv("tokens.env")

openai.api_key = os.getenv("OPENAI_API_KEY")

system_discussion = \
    "Envision a scenario where three UML experts are having a conversation about designing a UML class diagram to meet a specific user request. \n" \
    "In case of any ambiguities or uncertainties, the experts may need to seek clarification from the user. If such a situation arises, they should preface their question to the user with the statement \"QUESTION:\" and conclude it with \"END\", as in the following example:\n" \
    "QUESTION: <Insert question here> END\n" \
    "Please note, the user is not included in the generated conversation and should not be portrayed in the dialogue. Any inquiries made by the experts are to be formulated strictly following the above format." \
    "They should explore and discuss practical real-world scenarios. For example, they could delve into why it's often more logical to save a date of birth attribute and a method getAge rather than directly storing the age." \
    "Or when instead of saving a price and a discounted price it is often better to save a price and a discount and a method getDiscountedPrice." \
    "Encourage them to illustrate these concepts using concrete examples and clear logic."


class Message:
    def __init__(self, role: str, content: str):
        self.role = role
        self.content = content


def get_request_for_question_answering(messages: List[Message]):
    prompt = ""
    for message in messages:
        if message.role == "initial_prompt":
            prompt += f"User: {message.content}\n"
        elif message.role == "discussion":
            prompt += f"{message.content}\n"
        elif message.role == "answer":
            prompt += f"User: {message.content}\n"

    prompt += "User: "
    return prompt


def get_request_for_discussion(messages: List[Message]):
    current_messages = [
        {"role": "system", "content": system_discussion},
    ]

    for message in messages:
        if message.role == "initial_prompt":
            current_messages.append({"role": "user", "content": message.content})
        elif message.role == "discussion":
            current_messages.append({"role": "assistant", "content": message.content})
        elif message.role == "answer":
            current_messages.append({"role": "user", "content": message.content})

    return current_messages


def get_request_for_final_decision(messages: List[Message]):
    current_messages = [
        # {"role": "system", "content": system_discussion},
    ]

    for message in messages:
        if message.role == "initial_prompt":
            current_messages.append({"role": "user", "content": message.content})
        elif message.role == "discussion":
            current_messages.append({"role": "assistant", "content": message.content})
        elif message.role == "answer":
            current_messages.append({"role": "user", "content": message.content})

    current_messages.append({"role": "system",
                             "content": "The UML experts are now tasked with crafting a clear and precise step-by-step solution for the design of the class diagramm based on the discussion.\n "
                                        "It is crucial that the solution is highly sequential. The steps should instruct on elements such as creating, modifieng, deleting specific classes, defining attributes for these classes, creating associations between classes, and more.\n"
                                        "You do not need to add any review or process improvement steps. The experts are expected to be highly proficient in their field and are not required to review their work.\n"})

    return current_messages


def get_text_for_finetuning(messages: List[Message]):
    prompt = ""
    for message in messages:
        if message.role == "initial_prompt":
            prompt += f"### Request: {message.content}\n"
        elif message.role == "discussion":
            prompt += f"{message.content.replace('Expert', '### Expert').replace('QUESTION', '### QUESTION').replace('END', '')}\n"
        elif message.role == "answer":
            prompt += f"### Answer: {message.content}\n"
        elif message.role == "instructions":
            prompt += f"### Instructions: {message.content}\n"

    return prompt


def save_conversation(id: int, messages: List[Message]):
    with open(f"conversations/{id}.txt", "w") as file:
        file.write(get_text_for_finetuning(messages))


def start_discussion(id: int, request: str):
    messages = [Message("initial_prompt", request)]

    while True:
        discussion = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=get_request_for_discussion(messages),
            max_tokens=1536 + 128,
            temperature=1.3,
            top_p=1,
            frequency_penalty=0,
            presence_penalty=0.3,
            stop=["END", "User:"]
        )
        if discussion.choices[0].finish_reason == "length":
            raise Exception("The discussion is too long.")
        discussion_text = discussion.choices[0].message["content"]
        messages.append(Message("discussion", discussion_text))

        if "QUESTION" in discussion_text in discussion_text:
            answer = openai.Completion.create(
                model="text-davinci-003",
                prompt=get_request_for_question_answering(messages),
                max_tokens=1536 + 128,
                temperature=1.3,
                top_p=1,
                frequency_penalty=0,
                presence_penalty=0.3,
                stop=["QUESTION", "Expert"]
            )
            if discussion.choices[0].finish_reason == "length":
                raise Exception("The discussion is too long.")
            messages.append(Message("answer", answer.choices[0].text))
        else:
            final_decision = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=get_request_for_final_decision(messages),
                max_tokens=2048,
                temperature=1.3,
                top_p=1,
                frequency_penalty=0,
                presence_penalty=0.3,
            )

            if discussion.choices[0].finish_reason == "length":
                raise Exception("The discussion is too long.")

            final_decision_text = final_decision.choices[0].message["content"]
            messages.append(Message("instructions", final_decision_text))
            save_conversation(id, messages)
            break


if __name__ == '__main__':
    with open("customer_requests.txt", "r") as file:
        file_content = file.read()
        requests = [text.strip() for text in file_content.split("###") if text.strip() != ""]

        i = 0
        overall_error_count = 0
        for request in requests:
            error_count = 0
            try:
                start_discussion(i, request)
                i += 1
                overall_error_count = 0

                if i == 10:
                    break
            except Exception as e:
                print(f"Error {e}")
                time.sleep(5)
                error_count += 1
                overall_error_count += 1
                if error_count == 5:
                    continue
                if overall_error_count == 10:
                    print("To many errors. Stopping.")
                    break
