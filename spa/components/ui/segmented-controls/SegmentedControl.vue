<template>
<fieldset class="segmented-control t-neutral">
    <input id="radio_option1" class="segmented-control__input" type="radio" name="segment" value="Option 1">
    <label class="segmented-control__label segmented-control__label--start l-grid-33" for="radio_option1">Option 1</label>
    <input id="radio_option2" class="segmented-control__input" type="radio" name="segment" value="Option 2">
    <label class="segmented-control__label l-grid-33" for="radio_option2">Option 2</label>
    <input id="radio_option3" class="segmented-control__input" type="radio" name="segment" value="Option 3">
    <label class="segmented-control__label segmented-control__label--end l-grid-33" for="radio_option3">Option 3</label>
  </fieldset>
</template>

<script>
export default {
name: "SegmentedControl"
}
</script>

<style lang="scss" scoped>
/* Theming */
/* layout */
.l-grid-33 {
  float:left;
  width: 33.333%;
}
/* If the current object is clicked/tapped */
.t-neutral  input[type="radio"]:checked + label {
  background: $color--main-brand;
  border-color: $color--main-brand;
  color: #fff;
}

/* If the control has keyboard focus */
.t-neutral  input[type="radio"]:focus + label {
  background: $color--main-brand;
  color: #fff;
  outline:thin dotted;
}
.t-neutral  input[type="radio"]:active + label {
  color: #fff;
}

.t-neutral .segmented-control__label {
  border-bottom: 1px solid $color--main-brand;
  border-top: 1px solid $color--main-brand;
  background: white;
  color: #111;
}

.t-neutral .segmented-control__label {
  border-color: $color--main-brand;
}
/* Component */
.segmented-control__label {
  box-sizing: border-box;
  cursor: pointer;
  display: block;
  padding: 0.5em 0.75em;
  text-align: center;
  text-decoration: none;
  transition: background 0.4s ease, color 0.4s ease;
}
@media all and (min-width: 40em) {
  .segmented-control__label {
    padding: 0.75em;
  }
}



.segmented-control__label--start {
  border: 1px solid;
  border-radius: 5px 0 0 5px;
}
.segmented-control__label--end {
  border:1px solid;
  border-radius: 0 5px 5px 0;
}

</style>
