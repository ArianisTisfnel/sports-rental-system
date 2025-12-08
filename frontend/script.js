document.addEventListener('DOMContentLoaded', () => {
    // Example data
    const equipment = [
        { id: 1, name: 'Basketball', status: 'Available' },
        { id: 2, name: 'Tennis Racket', status: 'Rented' }
    ];

    const members = [
        { id: 1, name: 'John Doe', membership: 'Gold' },
        { id: 2, name: 'Jane Smith', membership: 'Silver' }
    ];

    const orders = [
        { id: 1, member: 'John Doe', equipment: 'Basketball', date: '2025-12-08' }
    ];

    // Populate equipment list
    const equipmentList = document.querySelector('#equipment-list ul');
    equipment.forEach(item => {
        const li = document.createElement('li');
        li.textContent = `${item.name} - ${item.status}`;
        equipmentList.appendChild(li);
    });

    // Populate member info
    const memberList = document.querySelector('#member-info ul');
    members.forEach(member => {
        const li = document.createElement('li');
        li.textContent = `${member.name} - ${member.membership}`;
        memberList.appendChild(li);
    });

    // Populate rental orders
    const orderList = document.querySelector('#rental-orders ul');
    orders.forEach(order => {
        const li = document.createElement('li');
        li.textContent = `Order #${order.id}: ${order.member} rented ${order.equipment} on ${order.date}`;
        orderList.appendChild(li);
    });
});