import torch
import streamlit as st
from kobart_transformers import get_kobart_tokenizer
from transformers import BartForConditionalGeneration

# Custom hash function to avoid caching issues with PyTorch models
def my_hash_func(_):
    return None

@st.cache_resource(hash_funcs={torch.nn.parameter.Parameter: my_hash_func, torch.Tensor: my_hash_func})
def load_model():
    model = BartForConditionalGeneration.from_pretrained('/Users/simmin-a/Desktop/project/newj/rsc/Binary_model/aihub')
    return model

model = load_model()
tokenizer = get_kobart_tokenizer()
st.title("KoBART 요약 Test")
text = st.text_area("뉴스 입력:")

st.markdown("## 뉴스 원문")
st.write(text)

if text:
    text = text.replace('\n', '')
    st.markdown("## KoBART 요약 결과")
    with st.spinner('processing..'):
        input_ids = tokenizer.encode(text)
        input_ids = torch.tensor(input_ids)
        input_ids = input_ids.unsqueeze(0)
        output = model.generate(input_ids, eos_token_id=1, max_length=512, num_beams=5)
        output = tokenizer.decode(output[0], skip_special_tokens=True)
    st.write(output)