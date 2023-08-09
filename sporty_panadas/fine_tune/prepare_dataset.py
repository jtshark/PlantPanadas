import os

from datasets import Dataset
from dotenv import load_dotenv
from transformers import AutoTokenizer
from itertools import chain
from functools import partial

load_dotenv("tokens.env")


def load_dataset():
    data = []
    for root, dirs, files in os.walk("plantuml"):
        for filename in files:
            with open(os.path.join(root, filename)) as file:
                data.append(file.read())
    return Dataset.from_dict({"text": data})


def template_dataset(sample):
    sample["text"] = f"{sample['text']}{tokenizer.eos_token}"
    return sample


remainder = {"input_ids": [], "attention_mask": [], "token_type_ids": []}


def chunk(sample, chunk_length=2048):
    # define global remainder variable to save remainder from batches to use in next batch
    global remainder
    # Concatenate all texts and add remainder from previous batch
    concatenated_examples = {k: list(chain(*sample[k])) for k in sample.keys()}
    concatenated_examples = {k: remainder[k] + concatenated_examples[k] for k in concatenated_examples.keys()}
    # get total number of tokens for batch
    batch_total_length = len(concatenated_examples[list(sample.keys())[0]])

    # get max number of chunks for batch
    if batch_total_length >= chunk_length:
        batch_chunk_length = (batch_total_length // chunk_length) * chunk_length

    # Split by chunks of max_len.
    result = {
        k: [t[i: i + chunk_length] for i in range(0, batch_chunk_length, chunk_length)]
        for k, t in concatenated_examples.items()
    }
    # add remainder to global variable for next batch
    remainder = {k: concatenated_examples[k][batch_chunk_length:] for k in concatenated_examples.keys()}
    # prepare labels
    result["labels"] = result["input_ids"].copy()
    return result


if __name__ == '__main__':
    model_id = "meta-llama/Llama-2-13b-hf"  # sharded weights
    tokenizer = AutoTokenizer.from_pretrained(model_id, use_auth_token=True)
    tokenizer.pad_token = tokenizer.eos_token

    dataset = load_dataset()

    dataset = dataset.map(template_dataset)

    lm_dataset = dataset.map(
        lambda sample: tokenizer(sample["text"]), batched=True, remove_columns=list(dataset.features)
    ).map(
        partial(chunk, chunk_length=2048),
        batched=True,
    )

    print(f"Total number of samples: {len(lm_dataset)}")

    training_input_path = f'prepared_conversations'
    lm_dataset.save_to_disk(training_input_path)
