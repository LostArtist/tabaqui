#!/usr/bin/env python3

import yaml
import textwrap
import argparse
from pathlib import Path

class SingleQuoted(str):
    pass

def single_quoted_representer(dumper, data):
    return dumper.represent_scalar('tag:yaml.org,2002:str', data, style="'")

yaml.add_representer(SingleQuoted, single_quoted_representer)
yaml.representer.SafeRepresenter.add_representer(SingleQuoted, single_quoted_representer)


def wrap_text(text, width=60):
    lines = text.split('\n')
    wrapped_lines = []
    for line in lines:
        wrapped_lines.extend(textwrap.wrap(line, width=width, replace_whitespace=False) or [""])
    return "\n".join(wrapped_lines)

def convert(input_path: Path, output_path: Path):
    with open(input_path, "r") as f:
        raw_data = yaml.safe_load(f)

    qna = {
        "version": 2,
        "created_by": "creator",
        "task_description": SingleQuoted("Teach the model about the Camel Kafka component"),
        "seed_examples": []
    }

    for item in raw_data:
        question = wrap_text(item["instruction"])
        answer = wrap_text(item["output"])
        qna["seed_examples"].append({
            "question": question,
            "answer": answer
        })

    with open(output_path, "w") as f:
        yaml.dump(qna, f, allow_unicode=True, width=60)

    print(f"✅ Converted to {output_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert .yaml Tabaqui snippets to qna.yaml for InstructLab.")
    parser.add_argument("input", help="Path to input YAML file (Tabaqui format)")
    parser.add_argument("output", help="Path to output qna.yaml")
    args = parser.parse_args()

    convert(Path(args.input), Path(args.output))
