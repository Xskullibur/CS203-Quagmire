// cypress/e2e/login.cy.ts

describe('Login Page', () => {
  beforeEach(() => {
    // Visit the login page before each test
    cy.visit('/auth/login')
  })

  it('should display login form', () => {
    cy.get('input[name="username"]').should('exist')
    cy.get('input[name="password"]').should('exist')
    cy.get('button[type="submit"]').should('exist')
  })

  it('should show error on invalid login', () => {
    cy.get('input[name="username"]').type('invaliduser')
    cy.get('input[name="password"]').type('invalidpassword')
    cy.get('form').submit()

    // Check for error message
    cy.get('.alert').should('contain', 'Invalid username or password')
  })

  it('should have a link to the registration page', () => {
    cy.contains('Don\'t have an account?').click()
    cy.url().should('include', '/auth/register')
  })

  it('should redirect to profile on successful login', () => {
    cy.intercept('POST', '**/authentication/login', {
      statusCode: 200,
      body: {
        userId: '100000',
        username: 'testuser',
        token: 'fake-jwt-token'
      }
    }).as('loginRequest')

    cy.get('input[name="username"]').type('testuser')
    cy.get('input[name="password"]').type('password123')
    cy.get('form').submit()

    cy.wait('@loginRequest')
    cy.url().should('include', '/profile')
  })
})