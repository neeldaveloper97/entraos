<template>
  <div class="flex-row" :class="showLine ? '': 'underline'">
    <div v-if="showLine" class="timeline pr-2 flex-column items-center timeline-left">
      <div class="full-circle" :class="[bgColor]" />
      <div class="vertical-line" :class="[bgColor]" />
    </div>
    <div class="flex-column full-width">
      <div class="timeline-content pb-1 flex-row space-between items-center">
          <span class="timePeriod font--description color--medium-emphasis">
            {{ fromDate }} - {{ toDate }}
          </span>
        <CloseButton v-if="!hideEdit" color="color--main-brand" editor-type="edit" @clicked="$emit('edit')" />
      </div>
      <p class="font--title-regular timeline-title pb-2">
        {{ title }}
      </p>
      <p class="font--title-regular pb-4" :class="headlineColor">
        {{ headline }}
      </p>
      <div v-if="formattedTechs.length >0" class="techs pb-2">
        <NormalButton v-for="(item, index) in formattedTechs" :key="index" is-light is-small class="mr-2 mb-2" :text="item" />
      </div>
      <p v-if="description.length > 0" class="timeline-summary pb-5">
        {{ formattedSummary }}
        <a v-if="displayShowmore" class="color--main-brand expand" @click="toggleShow">See More </a>
        <a v-if="displayShowLess" class="color--main-brand expand" @click="toggleShow">Hide</a>
      </p>
    </div>
  </div>
</template>

<script>
import CloseButton from "@/components/ui/buttons/EditorButton";

import NormalButton from "@/components/ui/buttons/NormalButton";
import colorMixin from "@/mixins/colorMixin";
export default {
  name: "Milestone",
  components: {
    CloseButton, NormalButton
  },
  mixins:[colorMixin],
  props:{
    fromDate:String,
    toDate:String,
    title:String,
    headline:String,
    headlineColor: {
      type:String,
      default:'color--main-brand'
    },
    description:String,
    showLine: {
      type:Boolean,
      default:true
    },
    showSummary:{
      type:Boolean,
      default:true
    },
    hideEdit:Boolean,
    techs:{
      type:Array,
      default: () => []
    }
  },

  data() {
    return {
      summaryShow:false
    }
  },
  computed:{
    displayShowmore(){
      return (this.summaryShow === false && this.description.length >= 120);
    },
    displayShowLess(){
      return (this.summaryShow && this.description.length >= 120)
    },
    formattedTechs(){
      return this.techs.filter(n => n)
    },
   formattedSummary(){
     if (this.summaryShow)
       return this.description;
     else
       return this.description.length >= 120 ?  this.description.substring(0,120) + '...' : this.description;
   }
  },
  mounted() {
    this.summaryShow = this.showSummary;
  },
  methods:{
    toggleShow(){
      this.summaryShow = !this.summaryShow;
    }
  }
}
</script>

<style lang="scss" scoped>
.timeline{
  top:4px;
  position: relative;
}
.full-circle {
  border-radius:100%;
  width:1rem;
  height:1rem;
}
.vertical-line{
  width:2px;
  height:100%;
}
.expand {
  white-space: nowrap
}

.underline:not(:last-child)
{
  border-bottom: 1px solid $color--line;
  margin-bottom:1.5rem;
}
</style>
