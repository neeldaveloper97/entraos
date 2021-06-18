<template>
  <div class="checkbox-container">
    <div class="cover" :class="unclickable ? 'unclickable' :''" />
    <div class="round">
      <input :id="id" type="checkbox" :value="value" :checked="checked" @change.prevent="$emit('input', {value:$event.target.value, checked: $event.target.checked} )" />
      <label :for="id"></label>
      <!--      <label for="checkbox">-->
      <!--        <input type="checkbox" :value="value" @change.prevent="$emit('input', {value:$event.target.value, checked: $event.target.checked} )">-->
      <!--      </label>-->
    </div>
  </div>
</template>

<script>
export default {
  name: "RoundedCheckBox",
  props: {
    value: String,
    checked: Boolean,
    unclickable: Boolean,
    id: String
  }
}
</script>

<style lang="scss" scoped>

.cover {
  width: 2rem;
  height: 2rem;
  background: transparent;
  position: absolute;
  z-index: 2;
  top: 0;
  left: 0;
  display: none;
}

.round {
  position: relative;
  width: 2rem;
  height: 2rem;
}

.round label {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 50%;
  cursor: pointer;
  height: 2rem;
  left: 0;
  position: absolute;
  top: 0;
  width: 2rem;
}

.round label:after {
  border: 2px solid #fff;
  border-top: none;
  border-right: none;
  content: "";
  height: 6px;
  left: 9px;
  opacity: 0;
  position: absolute;
  top: 10px;
  transform: rotate(-45deg);
  width: 12px;
}

.round input[type="checkbox"] {
  visibility: hidden;
}

.round input[type="checkbox"]:checked + label {
  background-color: $color--main-brand;
  border-color: $color--main-brand;
}

.round input[type="checkbox"]:checked + label:after {
  opacity: 1;
}


.unclickable {
  display: block;
}
.checkbox-container{
  position:relative;
  display:block;
}
</style>
