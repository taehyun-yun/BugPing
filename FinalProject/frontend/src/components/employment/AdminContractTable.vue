<template>
    <div class="table-container">
      <table class="employee-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Title</th>
            <th>Status</th>
            <th>Position</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="employee in employees" :key="employee.email">
            <td>
              <div class="employee-info">
                <img :src="employee.image" :alt="employee.name" class="avatar" />
                <div class="employee-details">
                  <p class="employee-name">{{ employee.name }}</p>
                  <p class="employee-email">{{ employee.email }}</p>
                </div>
              </div>
            </td>
            <td>
              <p class="title">{{ employee.title }}</p>
              <p class="department">{{ employee.department }}</p>
            </td>
            <td>
              <span :class="['status-badge', employee.status.toLowerCase()]">
                {{ employee.status }}
              </span>
            </td>
            <td>{{ employee.position }}</td>
            <td>
              <button class="edit-button">EDIT</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  const employees = [
    {
      name: 'John Doe',
      email: 'john.doe@gmail.com',
      image: 'https://mdbootstrap.com/img/new/avatars/8.jpg',
      title: 'Software engineer',
      department: 'IT department',
      status: 'Active',
      position: 'Senior'
    },
    {
      name: 'Alex Ray',
      email: 'alex.ray@gmail.com',
      image: 'https://mdbootstrap.com/img/new/avatars/6.jpg',
      title: 'Consultant',
      department: 'Finance',
      status: 'Onboarding',
      position: 'Junior'
    },
    {
      name: 'Kate Hunington',
      email: 'kate.hunington@gmail.com',
      image: 'https://mdbootstrap.com/img/new/avatars/7.jpg',
      title: 'Designer',
      department: 'UI/UX',
      status: 'Awaiting',
      position: 'Senior'
    }
  ]
  </script>
  
  <style scoped>
  .table-container {
    padding: 1rem;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }
  
  .employee-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
  }
  
  .employee-table th {
    padding: 12px 16px;
    text-align: left;
    background-color: #f8f9fa;
    color: #333;
    font-weight: 500;
    font-size: 0.875rem;
  }
  
  .employee-table td {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    vertical-align: middle;
  }
  
  .employee-info {
    display: flex;
    align-items: center;
    gap: 1rem;
  }
  
  .avatar {
    width: 45px;
    height: 45px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .employee-details {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .employee-name {
    margin: 0;
    font-weight: 600;
    color: #333;
  }
  
  .employee-email {
    margin: 0;
    color: #666;
    font-size: 0.875rem;
  }
  
  .title {
    margin: 0;
    color: #333;
    font-weight: 500;
  }
  
  .department {
    margin: 0;
    color: #666;
    font-size: 0.875rem;
  }
  
  .status-badge {
    padding: 6px 12px;
    border-radius: 50px;
    font-size: 0.75rem;
    font-weight: 500;
  }
  
  .status-badge.active {
    background-color: #e8f5e9;
    color: #2e7d32;
  }
  
  .status-badge.onboarding {
    background-color: #e3f2fd;
    color: #1976d2;
  }
  
  .status-badge.awaiting {
    background-color: #fff3e0;
    color: #f57c00;
  }
  
  .edit-button {
    padding: 6px 12px;
    border: none;
    background: none;
    color: #1976d2;
    font-weight: 600;
    cursor: pointer;
    font-size: 0.875rem;
  }
  
  .edit-button:hover {
    text-decoration: underline;
  }
  </style>