<template>
<textarea
    :value="value"
    class="textarea"
    :placeholder="placeholder"
    @input="$emit('input', $event.target.value)"
    @blur="$emit('blur')"
  />
</template>
<script>
export default {
name: "TextArea",
  props:{
    placeholder: {
      type: String,
      default:'e.g. Hello world'
    },
    value:{
      type:String
    },
  }
}
</script>
