<template>
  <div class="flex-column card">
    <nuxt-link :to="link">
      <div class="flex-column">
        <div class="card-row">
          <slot name="icon" />
        </div>
        <div class="card-row">
          <p class="font--title-regular">
            {{ text }}
          </p>
        </div>
      </div>
    </nuxt-link>
    <div class="flex-row card-description">
      <span class="font--description">{{ subText }}</span>
      <!--<span class="font--title-big" :class="color">{{ subValue }}</span> -->
      <font-awesome-icon class="color--main-brand card--right-arrow" :icon="['fas', 'angle-right']" />
    </div>
  </div>
</template>

<script>
export default {
  name: "DashboardCard",
  props: {
    link: {
      type: String
    },
    text: {
      type: [Number, String],
      required: true
    },
    subText: {
      type: [Number, String],
      required: true
    },
    subValue: {
      type: [Number, String]
    },
    color: {
      type: String,
      required: true
    }

  }
}
</script>

<style lang="scss" scoped>

.card-row {
  padding-bottom: 0.25rem;
}

.flex-column {
  display: flex;
  flex-direction: column;
  flex-basis: 100%;
  flex: 1;
}

.card-description {
  align-items: center;
}

.font--description {
  padding-right: 0.5rem;
}

.card--right-arrow {
  height: 100%;
  margin-left: auto;
  order: 2;
}

a {
  max-width: 100%;
  width: 100%;
  color: rgb(107, 108, 122);
  box-shadow: rgba(184, 186, 206, 0.06) 0px 4px 10px;
  border-radius: 10px;
  background-color: rgb(255, 255, 255);
  position: relative;
  text-align: left;
  transition: all 0.3s ease 0s;
  display: block;
}

.card {
  padding: 1rem;

  border-radius: 10px;
}

.card:not(:last-child) {
  margin-right: 1rem;
}

p {
  font-weight: bold;
  color: $color--high-emphasis;
}
</style>
