<template>
    <div class="frame">
      <div class="background">
        <div class="background2">
          <div class="rectangle-41"></div>
        </div>
        <div class="tabs">
          <div v-for="(tab, index) in tabs" :key="index" :class="tab.class">
            <div class="width-structure">
              <div class="height-structure">
                <div class="button-body">
                  <img :class="tab.iconClass" :src="tab.iconSrc" alt="Tab Icon" />
                  <div :class="tab.textClass">{{ tab.label }}</div>
                </div>
              </div>
              <div class="min-width">
                <div class="content"></div>
              </div>
            </div>
          </div>
        </div>
  
        <div class="avatar">
          <div class="name">
            <div class="email">{{ user.email }}</div>
            <div class="username">{{ user.name }}</div>
          </div>
          <div class="image">
            <div class="rectangle-36"></div>
            <div class="rectangle-37"></div>
            <img
              class="credits-to-unsplash-com"
              :src="user.avatar"
              alt="User Avatar"
            />
          </div>
          <div class="icon4">
            <div class="rectangle-42"></div>
            <img class="edit" :src="icons.edit" alt="Edit Icon" />
          </div>
        </div>
  
        <div class="background-border">
          <div class="container">
            <div class="div">전체</div>
            <div class="_1">1</div>
            <div class="vertical-divider"></div>
            <div class="listbox-listbox">
              <div class="background3">
                <div class="option">
                  <div class="div2">정렬</div>
                  <div class="div3">출근 시각</div>
                </div>
                <div class="select">
                  <div class="container2">
                    <img
                      class="component-10"
                      :src="icons.sort"
                      alt="Sort Icon"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="background-border2">
              <div class="image-vertical-border">
                <img
                  class="component-1"
                  :src="icons.early"
                  alt="Early Sorting Icon"
                />
              </div>
              <div class="image2">
                <img
                  class="component-12"
                  :src="icons.late"
                  alt="Late Sorting Icon"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "MyComponent",
    data() {
      return {
        tabs: [
          {
            label: "PROJECTS",
            class: "projects",
            iconClass: "icon",
            iconSrc: "@/assets/frontimages/icon0.svg",
            textClass: "text",
          },
          {
            label: "TEAMS",
            class: "teams",
            iconClass: "icon2",
            iconSrc: "@/assets/frontimages/icon1.svg",
            textClass: "text2",
          },
          {
            label: "OVERVIEW",
            class: "overview",
            iconClass: "icon3",
            iconSrc: "@/assets/frontimages/icon2.svg",
            textClass: "text",
          },
        ],
        user: {
          email: "esthera@simmmple.com",
          name: "Esthera Jackson",
          avatar: "@/assets/frontimages/credits-to-unsplash-com0.png",
        },
        icons: {
          edit: "@/assets/frontimages/edit0.svg",
          sort: "@/assets/frontimages/component-100.svg",
          early: "@/assets/frontimages/component-10.svg",
          late: "@/assets/frontimages/component-11.svg",
        },
      };
    },
  };
  </script>
  
  <style scoped>
  .frame {
    font-family: Arial, sans-serif;
    display: flex;
    flex-direction: column;
    margin: 20px;
  }
  
  .background {
    display: flex;
    flex-direction: column;
  }
  
  .tabs {
    display: flex;
    gap: 20px;
  }
  
  .projects,
  .teams,
  .overview {
    display: flex;
    flex-direction: column;
  }
  
  .button-body {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .avatar {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 20px;
  }
  
  .name .email {
    font-size: 12px;
    color: gray;
  }
  
  .name .username {
    font-size: 16px;
    font-weight: bold;
  }
  
  .image img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
  }
  
  .icon4 img {
    width: 20px;
    height: 20px;
  }
  </style>
  