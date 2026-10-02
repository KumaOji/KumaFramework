"""Generate the offline Lab catalog from repository controller/DTO sources (Python 3.10+)."""
import json
import re
from pathlib import Path

module = Path(__file__).resolve().parents[1]
source = module.parent / 'kuma-project-lab/src/main/java'
classes = {file.stem: file for file in source.rglob('*.java')}


def example(kind, depth=0):
    kind = kind.strip()
    if depth > 3:
        return None
    if kind in ('String', 'CharSequence'):
        return 'demo'
    if kind in ('int', 'long', 'short', 'Short', 'Integer', 'Long', 'double', 'float', 'Double', 'Float', 'BigDecimal'):
        return 1
    if kind in ('boolean', 'Boolean'):
        return False
    if kind.endswith('[]'):
        return [example(kind[:-2], depth + 1)]
    if kind.startswith(('List<', 'Set<')):
        return [example(kind[kind.index('<') + 1:-1], depth + 1)]
    if kind.startswith('Map<'):
        return {}
    if kind not in classes:
        return None
    text = classes[kind].read_text(encoding='utf-8-sig')
    if kind == 'KafkaScenarioDTO':
        return {'partitions': 3, 'messageCount': 6, 'replicationFactor': 1,
                'message': 'Hello Kafka\n观察生产、消费和 offset 提交', 'demonstrateRebalance': True}
    fields = re.findall(r'private\s+(?!static\b)([\w<>?,\[\] ]+?)\s+(\w+)\s*(?:=[^;]*)?;', text)
    if not fields:
        record = re.search(r'public record \w+\s*\(([\s\S]*?)\)\s*\{', text)
        if record:
            fields = re.findall(r'([\w<>?\[\]]+)\s+(\w+)\s*(?:,|$)', record[1])
    return {name: example(field_type, depth + 1) for field_type, name in fields}


catalog = []
for controller in sorted(source.rglob('*Controller.java')):
    text = controller.read_text(encoding='utf-8-sig')
    root = re.search(r'@RequestMapping\("(/lab[^" ]*)"\)', text)
    if not root:
        continue
    tag = re.search(r'@Tag\(name\s*=\s*"([^"]+)"', text)
    group = tag[1] if tag else controller.stem
    for mapping in re.finditer(r'@(Get|Post|Put|Delete|Patch)Mapping\("([^"]+)"\)', text):
        summary = re.findall(r'@Operation\(summary\s*=\s*"([^"]+)"', text[:mapping.start()])
        signature = text[mapping.end():text.find('{', mapping.end())]
        body = re.search(r'@RequestBody(?:\([^)]*\))?\s+(?:@Valid\s+)?([\w<>?,\[\]]+)\s+\w+', signature)
        catalog.append({'group': group, 'name': summary[-1] if summary else mapping[2],
                        'method': mapping[1].upper(), 'path': root[1] + mapping[2],
                        'body': example(body[1]) if body else '', 'source': controller.name})

target = module.parents[1] / 'kuma-fronted-console/renderer/lab-catalog.json'
target.write_text(json.dumps(catalog, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(f'Generated {len(catalog)} Lab endpoints: {target}')
